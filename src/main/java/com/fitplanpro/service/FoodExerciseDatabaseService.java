package com.fitplanpro.service;

import com.fitplanpro.dto.common.PageDto;
import com.fitplanpro.dto.fooddatabase.*;
import com.fitplanpro.dto.plan.ExerciseDto;
import com.fitplanpro.dto.profile.ProfileMetricsDto;
import com.fitplanpro.entity.Exercise;
import com.fitplanpro.entity.FoodDatabase;
import com.fitplanpro.enums.ExerciseDifficulty;
import com.fitplanpro.enums.GoalType;
import com.fitplanpro.enums.MuscleGroup;
import com.fitplanpro.mapper.FoodExerciseMapper;
import com.fitplanpro.repository.ExerciseRepository;
import com.fitplanpro.repository.FoodDatabaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for food and exercise database operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FoodExerciseDatabaseService {

    private final FoodDatabaseRepository foodDatabaseRepository;
    private final ExerciseRepository exerciseRepository;
    private final FoodExerciseMapper foodExerciseMapper;

    /**
     * Search foods by name
     *
     * @param query the search query
     * @param cultureRegion the culture/region filter (optional)
     * @param page the page number
     * @param size the page size
     * @return page of food search results
     */
    @Transactional(readOnly = true)
    public PageDto<FoodSearchResultDto> searchFoods(String query, String cultureRegion, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FoodDatabase> foodsPage;

        if (cultureRegion != null && !cultureRegion.isEmpty()) {
            foodsPage = foodDatabaseRepository.findByNameContainingIgnoreCaseAndCultureRegion(query, cultureRegion, pageable);
        } else {
            foodsPage = Page.empty();
            // This would be a custom repository method or use query method
            // foodsPage = foodDatabaseRepository.findByNameContainingIgnoreCase(query, pageable);
        }

        List<FoodSearchResultDto> content = foodExerciseMapper.toFoodSearchResultList(foodsPage.getContent());

        return PageDto.<FoodSearchResultDto>builder()
                .content(content)
                .page(foodsPage.getNumber())
                .size(foodsPage.getSize())
                .totalElements(foodsPage.getTotalElements())
                .totalPages(foodsPage.getTotalPages())
                .last(foodsPage.isLast())
                .build();
    }

    /**
     * Get food details by ID
     *
     * @param id the food ID
     * @return the food database DTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "foods", key = "#id")
    public FoodDatabaseDto getFoodById(Long id) {
        FoodDatabase food = foodDatabaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found with ID: " + id));

        return foodExerciseMapper.toFoodDto(food);
    }

    /**
     * Create a new food
     *
     * @param createDto the creation food DTO
     * @return the created food database DTO
     */
    @Transactional
    @CacheEvict(value = "foods", allEntries = true)
    public FoodDatabaseDto createFood(FoodDatabaseCreateDto createDto) {
        FoodDatabase food = foodExerciseMapper.toFoodEntity(createDto);
        FoodDatabase savedFood = foodDatabaseRepository.save(food);
        return foodExerciseMapper.toFoodDto(savedFood);
    }

    /**
     * Get foods by culture/region
     *
     * @param cultureRegion the culture/region
     * @return list of food database DTOs
     */
    @Transactional(readOnly = true)
    public List<FoodDatabaseDto> getFoodsByCultureRegion(String cultureRegion) {
        List<FoodDatabase> foods = foodDatabaseRepository.findByCultureRegion(cultureRegion);
        return foodExerciseMapper.toFoodDtoList(foods);
    }

    /**
     * Get traditional foods by culture/region
     *
     * @param cultureRegion the culture/region
     * @return list of food database DTOs
     */
    @Transactional(readOnly = true)
    public List<FoodDatabaseDto> getTraditionalFoods(String cultureRegion) {
        List<FoodDatabase> foods = foodDatabaseRepository.findByCultureRegionAndIsTraditionalTrue(cultureRegion);
        return foodExerciseMapper.toFoodDtoList(foods);
    }

    /**
     * Get foods suitable for a specific diet
     *
     * @param dietType the diet type
     * @param cultureRegion the culture/region
     * @return list of food database DTOs
     */
    @Transactional(readOnly = true)
    public List<FoodDatabaseDto> getFoodsForDiet(String dietType, String cultureRegion) {
        List<FoodDatabase> foods = foodDatabaseRepository.findFoodsSuitableForDiet(dietType, cultureRegion);
        return foodExerciseMapper.toFoodDtoList(foods);
    }

    /**
     * Get high protein foods
     *
     * @param cultureRegion the culture/region
     * @param minProtein the minimum protein content
     * @return list of food database DTOs
     */
    @Transactional(readOnly = true)
    public List<FoodDatabaseDto> getHighProteinFoods(String cultureRegion, float minProtein) {
        List<FoodDatabase> foods = foodDatabaseRepository.findHighProteinFoodsByCulture(cultureRegion, minProtein);
        return foodExerciseMapper.toFoodDtoList(foods);
    }

    /**
     * Search exercises by name
     *
     * @param query the search query
     * @return list of exercise search results
     */
    @Transactional(readOnly = true)
    public List<ExerciseSearchResultDto> searchExercises(String query) {
        List<Exercise> exercises = exerciseRepository.findByNameContainingIgnoreCase(query);
        return foodExerciseMapper.toExerciseSearchResultList(exercises);
    }

    /**
     * Get exercise details by ID
     *
     * @param id the exercise ID
     * @return the exercise database DTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "exercises", key = "#id")
    public ExerciseDto getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found with ID: " + id));

        return foodExerciseMapper.toExerciseDto(exercise);
    }

    /**
     * Create a new exercise
     *
     * @param createDto the create exercise DTO
     * @return the created exercise database DTO
     */
    @Transactional
    @CacheEvict(value = "exercises", allEntries = true)
    public ExerciseDto createExercise(ExerciseCreateDto createDto) {
        Exercise exercise = foodExerciseMapper.toExerciseEntity(createDto);
        Exercise savedExercise = exerciseRepository.save(exercise);
        return foodExerciseMapper.toExerciseDto(savedExercise);
    }

    /**
     * Get exercises by muscle group
     *
     * @param muscleGroup the muscle group
     * @return list of exercise database DTOs
     */
    @Transactional(readOnly = true)
    public List<ExerciseDto> getExercisesByMuscleGroup(MuscleGroup muscleGroup) {
        List<Exercise> exercises = exerciseRepository.findByMuscleGroup(muscleGroup);
        return foodExerciseMapper.toExerciseDtoList(exercises);
    }

    /**
     * Get exercises by difficulty
     *
     * @param difficulty the difficulty level
     * @return list of exercise database DTOs
     */
    @Transactional(readOnly = true)
    public List<ExerciseDto> getExercisesByDifficulty(ExerciseDifficulty difficulty) {
        List<Exercise> exercises = exerciseRepository.findByDifficulty(difficulty);
        return foodExerciseMapper.toExerciseDtoList(exercises);
    }

    /**
     * Get exercises by muscle group and difficulty
     *
     * @param muscleGroup the muscle group
     * @param difficulty the difficulty level
     * @return list of exercise database DTOs
     */
    @Transactional(readOnly = true)
    public List<ExerciseDto> getExercisesByMuscleGroupAndDifficulty(MuscleGroup muscleGroup, ExerciseDifficulty difficulty) {
        List<Exercise> exercises = exerciseRepository.findByMuscleGroupAndDifficulty(muscleGroup, difficulty);
        return foodExerciseMapper.toExerciseDtoList(exercises);
    }

    /**
     * Get bodyweight exercises
     *
     * @return list of exercise database DTOs
     */
    @Transactional(readOnly = true)
    public List<ExerciseDto> getBodyweightExercises() {
        List<Exercise> exercises = exerciseRepository.findBodyweightExercises();
        return foodExerciseMapper.toExerciseDtoList(exercises);
    }

    /**
     * Get exercises by equipment availability
     *
     * @param equipmentList comma-separated list of available equipment
     * @param difficulties list of acceptable difficulty levels
     * @param muscleGroups list of target muscle groups
     * @return list of exercise database DTOs
     */
    @Transactional(readOnly = true)
    public List<ExerciseDto> getExercisesByEquipment(String equipmentList, List<String> difficulties, List<String> muscleGroups) {
        List<Exercise> exercises = exerciseRepository.findExercisesByMuscleGroupsAndEquipment(
                muscleGroups, equipmentList, difficulties);
        return foodExerciseMapper.toExerciseDtoList(exercises);
    }

    /**
     * Get food recommendations based on user profile
     *
     * @param userProfileDto the user profile DTO
     * @param count the number of recommendations to return
     * @return list of food recommendation DTOs
     */
    @Transactional(readOnly = true)
    public List<FoodRecommendationDto> getFoodRecommendations(ProfileMetricsDto userProfileDto, int count) {
        // Get foods suitable for diet
        List<FoodDatabase> dietFoods = foodDatabaseRepository.findFoodsSuitableForDiet(
                userProfileDto.getDietPreference().toString(),
                "MENA" // Default region, would use actual user region
        );

        // Get high protein foods if weight loss goal
        if (userProfileDto.getGoalType() == GoalType.WEIGHT_LOSS ||
                userProfileDto.getGoalType() == GoalType.MUSCLE_GAIN) {
            List<FoodDatabase> proteinFoods = foodDatabaseRepository.findHighProteinFoodsByCulture("MENA", 15.0f);

            // Combine lists
            dietFoods.addAll(proteinFoods);
        }

        // Limit results
        List<FoodDatabase> recommendedFoods = dietFoods.stream()
                .distinct()
                .limit(count)
                .toList();

        // Convert to recommendation DTOs
        return recommendedFoods.stream()
                .map(food -> {
                    FoodRecommendationDto recommendation = foodExerciseMapper.toFoodRecommendation(food);

                    // Set recommendation reason based on food properties
                    if (food.getProteinPer100g() > 15.0f) {
                        recommendation.setRecommendationReason("High protein content to support your " +
                                userProfileDto.getGoalType().toString().toLowerCase() + " goal");
                        recommendation.setNutritionalHighlight("Excellent protein source");
                    } else if (food.getIsTraditional()) {
                        recommendation.setRecommendationReason("Traditional food that fits your dietary preferences");
                        recommendation.setCulturalRelevance("Traditional dish");
                    } else {
                        recommendation.setRecommendationReason("Fits your " +
                                userProfileDto.getDietPreference().toString().toLowerCase() + " diet");
                    }

                    recommendation.setMatchScore(0.9f); // Placeholder score
                    recommendation.setDietaryMatch(userProfileDto.getDietPreference().toString());

                    return recommendation;
                })
                .toList();
    }

    /**
     * Get exercise recommendations based on user profile
     *
     * @param userProfileDto the user profile DTO
     * @param count the number of recommendations to return
     * @return list of exercise recommendation DTOs
     */
    @Transactional(readOnly = true)
    public List<ExerciseRecommendationDto> getExerciseRecommendations(ProfileMetricsDto userProfileDto, int count) {
        // Get appropriate difficulty level
        ExerciseDifficulty difficulty = switch (userProfileDto.getTrainingExperience()) {
            case BEGINNER -> ExerciseDifficulty.BEGINNER;
            case INTERMEDIATE -> ExerciseDifficulty.INTERMEDIATE;
            case ADVANCED -> ExerciseDifficulty.ADVANCED;
            default -> ExerciseDifficulty.BEGINNER;
        };

        // Get exercises matching difficulty
        List<Exercise> exercises = exerciseRepository.findByDifficulty(difficulty);

        // Limit results
        List<Exercise> recommendedExercises = exercises.stream()
                .limit(count)
                .toList();

        // Convert to recommendation DTOs
        return recommendedExercises.stream()
                .map(exercise -> {
                    ExerciseRecommendationDto recommendation = foodExerciseMapper.toExerciseRecommendation(exercise);

                    // Set recommendation reason based on exercise properties
                    recommendation.setRecommendationReason("Matches your " +
                            userProfileDto.getTrainingExperience().toString().toLowerCase() + " experience level");
                    recommendation.setMatchScore(0.9f); // Placeholder score
                    recommendation.setTargetMuscleGroup(exercise.getMuscleGroup().toString());
                    recommendation.setEquipmentMatched("Standard equipment");
                    recommendation.setDifficultyLevel(exercise.getDifficulty().toString());

                    return recommendation;
                })
                .toList();
    }
}