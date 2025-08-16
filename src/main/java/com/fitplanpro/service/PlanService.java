package com.fitplanpro.service;

import com.fitplanpro.dto.aiservice.AIServiceRequestDto;
import com.fitplanpro.dto.aiservice.AIServiceResponseDto;
import com.fitplanpro.dto.plan.*;
import com.fitplanpro.enums.PromptTemplateType;
import com.fitplanpro.exception.PlanNotFoundException;
import com.fitplanpro.exception.ProfileNotFoundException;
import com.fitplanpro.mapper.PlanMapper;
import com.fitplanpro.entity.Plan;
import com.fitplanpro.enums.PlanType;
import com.fitplanpro.entity.UserProfile;
import com.fitplanpro.repository.PlanRepository;
import com.fitplanpro.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for plan operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlanService {

    private final PlanRepository planRepository;
    private final UserProfileRepository userProfileRepository;
    private final PlanMapper planMapper;
    private final UserService userService;
    private final AIService aiService;

    /**
     * Generate a new plan
     *
     * @param requestDto the plan generation request
     * @return the generation result DTO
     */
    @Transactional
    @CacheEvict(value = "plans", allEntries = true)
    public PlanGenerationResultDto generatePlan(PlanGenerationRequestDto requestDto) {
        // Get user profile
        UserProfile userProfile = userProfileRepository.findById(requestDto.getProfileId())
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID: " + requestDto.getProfileId()));

        // Check if a plan already exists for this profile
        if (!Boolean.TRUE.equals(requestDto.getForceRegenerate())) {
            Optional<Plan> existingPlan = planRepository.findTopByProfileHashAndPlanTypeOrderByGenerationTimeDesc(
                    userProfile.getProfileHash(), requestDto.getPlanType());

            if (existingPlan.isPresent()) {
                // Return existing plan
                return PlanGenerationResultDto.builder()
                        .planId(existingPlan.get().getId())
                        .planType(existingPlan.get().getPlanType())
                        .fromCache(true)
                        .similarityScore(existingPlan.get().getSimilarityScore())
                        .aiModelUsed(existingPlan.get().getAiModelUsed())
                        .generationTimeMs(0L)
                        .build();
            }
        }

        // Search for similar plans
        List<Plan> similarPlans = findSimilarPlans(userProfile, requestDto.getPlanType());

        if (!similarPlans.isEmpty() && !Boolean.TRUE.equals(requestDto.getForceRegenerate())) {
            // Use the most similar plan
            Plan mostSimilarPlan = similarPlans.get(0);

            // Return similar plan
            return PlanGenerationResultDto.builder()
                    .planId(mostSimilarPlan.getId())
                    .planType(mostSimilarPlan.getPlanType())
                    .fromCache(true)
                    .similarityScore(mostSimilarPlan.getSimilarityScore())
                    .aiModelUsed(mostSimilarPlan.getAiModelUsed())
                    .generationTimeMs(0L)
                    .build();
        }

        // Generate plan using AI service
        long startTime = System.currentTimeMillis();

        AIServiceRequestDto aiRequest = createAIRequest(userProfile, requestDto.getPlanType());
        AIServiceResponseDto aiResponse = aiService.generateContent(aiRequest);

        long endTime = System.currentTimeMillis();
        long generationTime = endTime - startTime;

        // Create new plan
        Plan plan = new Plan();
        plan.setProfileHash(userProfile.getProfileHash());
        plan.setPlanType(requestDto.getPlanType());
        plan.setPlanData(aiResponse.getResponse());
        plan.setAiModelUsed(aiResponse.getModelUsed());
        plan.setGenerationTime(LocalDateTime.now());
        plan.setExpiryTime(LocalDateTime.now().plusDays(30)); // Plans expire after 30 days
        plan.setSimilarityScore(1.0f); // Perfect match

        // Save plan
        Plan savedPlan = planRepository.save(plan);

        // Return generation result
        return PlanGenerationResultDto.builder()
                .planId(savedPlan.getId())
                .planType(savedPlan.getPlanType())
                .fromCache(false)
                .similarityScore(savedPlan.getSimilarityScore())
                .aiModelUsed(savedPlan.getAiModelUsed())
                .generationTimeMs(generationTime)
                .build();
    }

    /**
     * Get plan summary by ID
     *
     * @param id the plan ID
     * @return the plan summary DTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "plans", key = "#id")
    public PlanSummaryDto getPlanSummary(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found with ID: " + id));

        return planMapper.toSummaryDto(plan);
    }

    /**
     * Get meal plan by ID
     *
     * @param id the plan ID
     * @return the meal plan DTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "mealPlans", key = "#id")
    public MealPlanDto getMealPlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found with ID: " + id));

        if (plan.getPlanType() != PlanType.MEAL_PLAN && plan.getPlanType() != PlanType.COMBINED_PLAN) {
            throw new PlanNotFoundException("No meal plan found with ID: " + id);
        }

        MealPlanDto mealPlanDto = planMapper.toMealPlanDto(plan);
        return planMapper.processMealPlanData(mealPlanDto, plan.getPlanData());
    }

    /**
     * Get workout plan by ID
     *
     * @param id the plan ID
     * @return the workout plan DTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "workoutPlans", key = "#id")
    public WorkoutPlanDto getWorkoutPlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found with ID: " + id));

        if (plan.getPlanType() != PlanType.WORKOUT_PLAN && plan.getPlanType() != PlanType.COMBINED_PLAN) {
            throw new PlanNotFoundException("No workout plan found with ID: " + id);
        }

        WorkoutPlanDto workoutPlanDto = planMapper.toWorkoutPlanDto(plan);
        return planMapper.processWorkoutPlanData(workoutPlanDto, plan.getPlanData());
    }

    /**
     * Get combined plan by ID
     *
     * @param id the plan ID
     * @return the combined plan DTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "combinedPlans", key = "#id")
    public CombinedPlanDto getCombinedPlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found with ID: " + id));

        if (plan.getPlanType() != PlanType.COMBINED_PLAN) {
            throw new PlanNotFoundException("No combined plan found with ID: " + id);
        }

        // Convert plan to DTOs
        CombinedPlanDto combinedPlanDto = planMapper.toCombinedPlanDto(plan);

        // Process meal plan
        MealPlanDto mealPlanDto = planMapper.toMealPlanDto(plan);
        mealPlanDto = planMapper.processMealPlanData(mealPlanDto, plan.getPlanData());

        // Process workout plan
        WorkoutPlanDto workoutPlanDto = planMapper.toWorkoutPlanDto(plan);
        workoutPlanDto = planMapper.processWorkoutPlanData(workoutPlanDto, plan.getPlanData());

        // Set meal and workout plans
        combinedPlanDto.setMealPlan(mealPlanDto);
        combinedPlanDto.setWorkoutPlan(workoutPlanDto);

        // Set recommendations
        @SuppressWarnings("unchecked")
        Map<String, Object> recommendations = (Map<String, Object>) plan.getPlanData().get("recommendations");
        combinedPlanDto.setRecommendations(recommendations);

        return combinedPlanDto;
    }

    /**
     * Get user's active plans
     *
     * @return list of plan summary DTOs
     */
    @Transactional(readOnly = true)
    public List<PlanSummaryDto> getUserActivePlans() {
        // Get current user
        userService.getCurrentUserEntity();

        // Get user's profiles
        List<UserProfile> userProfiles = userProfileRepository.findByUser(userService.getCurrentUserEntity());

        if (userProfiles.isEmpty()) {
            return List.of();
        }

        // Get profile hashes
        List<String> profileHashes = userProfiles.stream()
                .map(UserProfile::getProfileHash)
                .toList();

        // Get active plans for profiles
        List<Plan> activePlans = profileHashes.stream()
                .flatMap(hash -> planRepository.findByProfileHash(hash).stream())
                .filter(plan -> plan.getExpiryTime() == null || plan.getExpiryTime().isAfter(LocalDateTime.now()))
                .toList();

        return planMapper.toSummaryDtoList(activePlans);
    }

    /**
     * Delete plan
     *
     * @param id the plan ID
     * @return true if plan was deleted successfully
     */
    @Transactional
    @CacheEvict(value = {"plans", "mealPlans", "workoutPlans", "combinedPlans"}, key = "#id")
    public boolean deletePlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found with ID: " + id));

        planRepository.delete(plan);

        return true;
    }

    /**
     * Find similar plans for a profile
     *
     * @param userProfile the user profile
     * @param planType the plan type
     * @return list of similar plans
     */
    private List<Plan> findSimilarPlans(UserProfile userProfile, PlanType planType) {
        // Calculate ranges for similarity search
        int ageMin = userProfile.getAge() - 3;
        int ageMax = userProfile.getAge() + 3;
        float weightMin = userProfile.getCurrentWeight() - 8;
        float weightMax = userProfile.getCurrentWeight() + 8;
        float heightMin = userProfile.getHeight() - 8;
        float heightMax = userProfile.getHeight() + 8;

        // Find similar plans
        return planRepository.findSimilarPlans(
                userProfile.getGender().toString(),
                ageMin,
                ageMax,
                userProfile.getGoalType().toString(),
                weightMin,
                weightMax,
                heightMin,
                heightMax,
                planType.toString(),
                userProfile.getCurrentWeight(),
                userProfile.getAge());
    }

    /**
     * Create AI service request for plan generation
     *
     * @param userProfile the user profile
     * @param planType the plan type
     * @return the AI service request DTO
     */
    private AIServiceRequestDto createAIRequest(UserProfile userProfile, PlanType planType) {
        // Prepare parameters
       Map<String, Object> parameters = Map.ofEntries(
                Map.entry("gender", userProfile.getGender().toString()),
                Map.entry("age", userProfile.getAge()),
                Map.entry("height", userProfile.getHeight()),
                Map.entry("current_weight", userProfile.getCurrentWeight()),
                Map.entry("target_weight", userProfile.getTargetWeight()),
                Map.entry("goal_type", userProfile.getGoalType().toString()),
                Map.entry("training_experience", userProfile.getTrainingExperience().toString()),
                Map.entry("training_location", userProfile.getTrainingLocation().toString()),
                Map.entry("days_per_week", userProfile.getDaysPerWeek()),
                Map.entry("diet_preference", userProfile.getDietPreference().toString()),
                Map.entry("meals_per_day", userProfile.getMealsPerDay()),
                Map.entry("snacks_per_day", userProfile.getSnacksPerDay()),
                Map.entry("location_culture", userProfile.getLocationCulture()),
                Map.entry("medical_conditions", userProfile.getMedicalConditions()),
                Map.entry("food_allergies", userProfile.getFoodAllergies()),
                Map.entry("time_per_workout", userProfile.getTimePerWorkout()),
                Map.entry("equipment_available", userProfile.getEquipmentAvailable()),
                Map.entry("budget_constraints", userProfile.getBudgetConstraints() != null ?
                        userProfile.getBudgetConstraints().toString() : null),
                Map.entry("preferred_workout_time", userProfile.getPreferredWorkoutTime() != null ?
                        userProfile.getPreferredWorkoutTime().toString() : null)
        );

        // Create AI service request
        return AIServiceRequestDto.builder()
                .templateType(planType == PlanType.MEAL_PLAN ?
                        PromptTemplateType.MEAL_PLAN :
                        planType == PlanType.WORKOUT_PLAN ?
                                PromptTemplateType.WORKOUT_PLAN :
                                PromptTemplateType.COMBINED_PLAN)
                .parameters(parameters)
                .maxTokens(4000)
                .temperature(0.7)
                .build();
    }
}