package com.fitplanpro.mapper;


import com.fitplanpro.dto.plan.*;
import com.fitplanpro.entity.Plan;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlanMapper {

    /**
     * Convert Plan entity to PlanSummaryDto
     *
     * @param plan the entity to convert
     * @return the summary DTO
     */
    PlanSummaryDto toSummaryDto(Plan plan);

    /**
     * Convert Plan entity to MealPlanDto
     *
     * @param plan the entity to convert
     * @return the meal plan DTO
     */
    @Mapping(target = "dailyCalories", ignore = true)
    @Mapping(target = "macros", ignore = true)
    @Mapping(target = "dailyPlans", ignore = true)
    MealPlanDto toMealPlanDto(Plan plan);

    /**
     * Convert Plan entity to WorkoutPlanDto
     *
     * @param plan the entity to convert
     * @return the workout plan DTO
     */
    @Mapping(target = "weeklySchedule", ignore = true)
    @Mapping(target = "focus", ignore = true)
    @Mapping(target = "recommendedEquipment", ignore = true)
    WorkoutPlanDto toWorkoutPlanDto(Plan plan);

    /**
     * Convert Plan entity to CombinedPlanDto
     *
     * @param plan the entity to convert
     * @return the combined plan DTO
     */
    @Mapping(target = "mealPlan", ignore = true)
    @Mapping(target = "workoutPlan", ignore = true)
    @Mapping(target = "recommendations", ignore = true)
    CombinedPlanDto toCombinedPlanDto(Plan plan);

    /**
     * Convert a list of Plan entities to a list of PlanSummaryDtos
     *
     * @param plans the list of entities
     * @return the list of summary DTOs
     */
    List<PlanSummaryDto> toSummaryDtoList(List<Plan> plans);

    /**
     * Process meal plan data from JSON to structured DTO
     *
     * @param mealPlanDto the base meal plan DTO
     * @param planData the JSON plan data
     * @return the fully populated meal plan DTO
     */
    @AfterMapping
    default MealPlanDto processMealPlanData(MealPlanDto mealPlanDto, @Context Map<String, Object> planData) {
        if (planData == null) {
            return mealPlanDto;
        }

        // Extract daily calories
        if (planData.containsKey("daily_calories")) {
            mealPlanDto.setDailyCalories((Integer) planData.get("daily_calories"));
        }

        // Extract macro split
        if (planData.containsKey("macros")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> macroData = (Map<String, Object>) planData.get("macros");

            MacroSplitDto macroSplitDto = MacroSplitDto.builder()
                    .proteinPercentage((Integer) macroData.get("protein_percentage"))
                    .carbsPercentage((Integer) macroData.get("carbs_percentage"))
                    .fatsPercentage((Integer) macroData.get("fats_percentage"))
                    .proteinGrams((Integer) macroData.get("protein_grams"))
                    .carbsGrams((Integer) macroData.get("carbs_grams"))
                    .fatsGrams((Integer) macroData.get("fats_grams"))
                    .build();

            mealPlanDto.setMacros(macroSplitDto);
        }

        // Extract daily plans
        if (planData.containsKey("daily_plans")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> dailyPlansData = (List<Map<String, Object>>) planData.get("daily_plans");

            List<DailyMealPlanDto> dailyPlans = dailyPlansData.stream().map(dailyPlanData -> {
                Integer day = (Integer) dailyPlanData.get("day");
                Integer totalCalories = (Integer) dailyPlanData.get("total_calories");

                // Process meals
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> mealsData = (List<Map<String, Object>>) dailyPlanData.get("meals");

                List<MealDto> meals = mealsData.stream().map(mealData -> {
                    @SuppressWarnings("unchecked")
                    List<String> ingredients = (List<String>) mealData.get("ingredients");

                    @SuppressWarnings("unchecked")
                    Map<String, Integer> macros = (Map<String, Integer>) mealData.get("macros");

                    return MealDto.builder()
                            .mealNumber((Integer) mealData.get("meal_number"))
                            .name((String) mealData.get("name"))
                            .ingredients(ingredients)
                            .preparation((String) mealData.get("preparation"))
                            .calories((Integer) mealData.get("calories"))
                            .macros(macros)
                            .build();
                }).toList();

                // Process total macros
                @SuppressWarnings("unchecked")
                Map<String, Object> totalMacrosData = (Map<String, Object>) dailyPlanData.get("total_macros");

                MacroSplitDto totalMacros = MacroSplitDto.builder()
                        .proteinPercentage((Integer) totalMacrosData.get("protein_percentage"))
                        .carbsPercentage((Integer) totalMacrosData.get("carbs_percentage"))
                        .fatsPercentage((Integer) totalMacrosData.get("fats_percentage"))
                        .proteinGrams((Integer) totalMacrosData.get("protein_grams"))
                        .carbsGrams((Integer) totalMacrosData.get("carbs_grams"))
                        .fatsGrams((Integer) totalMacrosData.get("fats_grams"))
                        .build();

                return DailyMealPlanDto.builder()
                        .day(day)
                        .meals(meals)
                        .totalCalories(totalCalories)
                        .totalMacros(totalMacros)
                        .build();
            }).toList();

            mealPlanDto.setDailyPlans(dailyPlans);
        }

        return mealPlanDto;
    }

    /**
     * Process workout plan data from JSON to structured DTO
     *
     * @param workoutPlanDto the base workout plan DTO
     * @param planData the JSON plan data
     * @return the fully populated workout plan DTO
     */
    @AfterMapping
    default WorkoutPlanDto processWorkoutPlanData(WorkoutPlanDto workoutPlanDto, @Context Map<String, Object> planData) {
        if (planData == null) {
            return workoutPlanDto;
        }

        // Extract general info
        if (planData.containsKey("focus")) {
            workoutPlanDto.setFocus((String) planData.get("focus"));
        }

        if (planData.containsKey("recommended_equipment")) {
            workoutPlanDto.setRecommendedEquipment((String) planData.get("recommended_equipment"));
        }

        // Extract weekly schedule
        if (planData.containsKey("weekly_schedule")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> weeklyScheduleData = (List<Map<String, Object>>) planData.get("weekly_schedule");

            List<DailyWorkoutDto> weeklySchedule = weeklyScheduleData.stream().map(dailyWorkoutData -> {
                Integer day = (Integer) dailyWorkoutData.get("day");
                String focus = (String) dailyWorkoutData.get("focus");
                Integer estimatedDuration = (Integer) dailyWorkoutData.get("estimated_duration");
                String warmup = (String) dailyWorkoutData.get("warmup");
                String cooldown = (String) dailyWorkoutData.get("cooldown");

                // Process exercises
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> exercisesData = (List<Map<String, Object>>) dailyWorkoutData.get("exercises");

                List<ExerciseDto> exercises = exercisesData.stream().map(exerciseData -> {
                    return ExerciseDto.builder()
                            .name((String) exerciseData.get("name"))
                            .sets((Integer) exerciseData.get("sets"))
                            .reps((String) exerciseData.get("reps"))
                            .restSeconds((Integer) exerciseData.get("rest_seconds"))
                            .notes((String) exerciseData.get("notes"))
                            .alternativeExercise((String) exerciseData.get("alternative_exercise"))
                            .build();
                }).toList();

                return DailyWorkoutDto.builder()
                        .day(day)
                        .focus(focus)
                        .exercises(exercises)
                        .estimatedDuration(estimatedDuration)
                        .warmup(warmup)
                        .cooldown(cooldown)
                        .build();
            }).toList();

            workoutPlanDto.setWeeklySchedule(weeklySchedule);
        }

        return workoutPlanDto;
    }
}