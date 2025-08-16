package com.fitplanpro.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitplanpro.dto.tracking.*;
import com.fitplanpro.entity.*;
import com.fitplanpro.enums.PhotoType;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public abstract class TrackingMapper {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Convert TrackingData entity to DailyTrackingDto
     */
    @Mapping(target = "meals", expression = "java(mapMealsFromJson(trackingData.getMeals()))")
    @Mapping(target = "workout", expression = "java(mapWorkoutFromJson(trackingData.getWorkouts()))")
    public abstract DailyTrackingDto toDto(Tracking trackingData);

    /**
     * Convert DailyTrackingDto to TrackingData entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "meals", expression = "java(mapMealsToJson(trackingDto.getMeals()))")
    @Mapping(target = "workouts", expression = "java(mapWorkoutToJson(trackingDto.getWorkout()))")
    @Mapping(target = "createdAt", ignore = true)
    public abstract Tracking toEntity(DailyTrackingDto trackingDto, User user);

    /**
     * Update TrackingData entity with DailyTrackingDto
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "meals", expression = "java(mapMealsToJson(trackingDto.getMeals()))")
    @Mapping(target = "workouts", expression = "java(mapWorkoutToJson(trackingDto.getWorkout()))")
    @Mapping(target = "createdAt", ignore = true)
    public abstract void updateEntity(DailyTrackingDto trackingDto, @MappingTarget Tracking trackingData);

    /**
     * Convert BodyMeasurement entity to BodyMeasurementDto
     */
    public abstract BodyMeasurementDto toBodyMeasurementDto(BodyMeasurement bodyMeasurement);

    /**
     * Convert BodyMeasurementDto to BodyMeasurement entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", ignore = true)
    public abstract BodyMeasurement toBodyMeasurementEntity(BodyMeasurementDto measurementDto, User user);

    /**
     * Convert ProgressPhoto entity to ProgressPhotoDto
     */
    @Mapping(target = "photoType", source = "photoType", qualifiedByName = "photoTypeToString")
    public abstract ProgressPhotoDto toProgressPhotoDto(ProgressPhoto progressPhoto);

    /**
     * Convert ProgressPhotoDto to ProgressPhoto entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "photoType", source = "photoDto.photoType", qualifiedByName = "stringToPhotoType")
    @Mapping(target = "createdAt", ignore = true)
    public abstract ProgressPhoto toProgressPhotoEntity(ProgressPhotoDto photoDto, User user);

    /**
     * Convert WorkoutHistory entity to WorkoutLogDto
     */
    @Mapping(target = "exercises", expression = "java(mapExercisesFromJson(workoutHistory.getExercises()))")
    @Mapping(target = "completed", constant = "true")
    @Mapping(target = "plannedWorkout", constant = "false")
    public abstract WorkoutLogDto toWorkoutLogDto(WorkoutHistory workoutHistory);

    /**
     * Convert WorkoutLogDto to WorkoutHistory entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "workoutDate", expression = "java(mapWorkoutDate(workoutLogDto))")
    @Mapping(target = "exercises", expression = "java(mapExercisesToJson(workoutLogDto.getExercises()))")
    @Mapping(target = "createdAt", ignore = true)
    public abstract WorkoutHistory toWorkoutHistoryEntity(WorkoutLogDto workoutLogDto, User user);

    /**
     * Extract workout date from WorkoutLogDto
     */
    protected LocalDate mapWorkoutDate(WorkoutLogDto workoutLogDto) {
        return LocalDate.now(); // Default to current date if not specified
    }

    /**
     * Convert PhotoType enum to String
     */
    @Named("photoTypeToString")
    protected String photoTypeToString(PhotoType photoType) {
        return photoType != null ? photoType.name() : null;
    }

    /**
     * Convert String to PhotoType enum
     */
    @Named("stringToPhotoType")
    protected PhotoType stringToPhotoType(String photoType) {
        return photoType != null ? PhotoType.valueOf(photoType) : null;
    }

    /**
     * Convert a list of TrackingData entities to a list of DailyTrackingDtos
     */
    public abstract List<DailyTrackingDto> toDtoList(List<Tracking> trackingDataList);

    /**
     * Convert a list of BodyMeasurement entities to a list of BodyMeasurementDtos
     */
    public abstract List<BodyMeasurementDto> toBodyMeasurementDtoList(List<BodyMeasurement> measurements);

    /**
     * Convert a list of ProgressPhoto entities to a list of ProgressPhotoDtos
     */
    public abstract List<ProgressPhotoDto> toProgressPhotoDtoList(List<ProgressPhoto> photos);

    /**
     * Convert a list of WorkoutHistory entities to a list of WorkoutLogDtos
     */
    @IterableMapping(qualifiedByName = "workoutHistoryToLogDto")
    public abstract List<WorkoutLogDto> toWorkoutLogDtoList(List<WorkoutHistory> workoutHistories);

    @Named("workoutHistoryToLogDto")
    protected WorkoutLogDto workoutHistoryToLogDto(WorkoutHistory workoutHistory) {
        WorkoutLogDto dto = toWorkoutLogDto(workoutHistory);
        return dto;
    }

    /**
     * Map meals from JSON to DTO list
     */
    protected List<MealLogDto> mapMealsFromJson(Map<String, Object> mealsJson) {
        if (mealsJson == null) {
            return new ArrayList<>();
        }

        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mealsList = (List<Map<String, Object>>) mealsJson.get("meals");

            if (mealsList == null) {
                return new ArrayList<>();
            }

            List<MealLogDto> mealLogDtos = new ArrayList<>();

            for (Map<String, Object> mealMap : mealsList) {
                MealLogDto mealLogDto = new MealLogDto();

                mealLogDto.setMealNumber(getIntegerValue(mealMap, "mealNumber"));
                mealLogDto.setMealType((String) mealMap.get("mealType"));
                mealLogDto.setName((String) mealMap.get("name"));
                mealLogDto.setPhotoUrl((String) mealMap.get("photoUrl"));
                mealLogDto.setTotalCalories(getIntegerValue(mealMap, "totalCalories"));
                mealLogDto.setNotes((String) mealMap.get("notes"));
                mealLogDto.setPlannedMeal(getBooleanValue(mealMap, "plannedMeal"));

                @SuppressWarnings("unchecked")
                Map<String, Integer> macros = (Map<String, Integer>) mealMap.get("macros");
                mealLogDto.setMacros(macros);

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> foodItemsList = (List<Map<String, Object>>) mealMap.get("foodItems");

                if (foodItemsList != null) {
                    List<FoodItemDto> foodItems = new ArrayList<>();

                    for (Map<String, Object> foodItemMap : foodItemsList) {
                        FoodItemDto foodItemDto = new FoodItemDto();

                        foodItemDto.setName((String) foodItemMap.get("name"));
                        foodItemDto.setQuantity(getIntegerValue(foodItemMap, "quantity"));
                        foodItemDto.setUnit((String) foodItemMap.get("unit"));
                        foodItemDto.setCalories(getIntegerValue(foodItemMap, "calories"));

                        @SuppressWarnings("unchecked")
                        Map<String, Integer> foodMacros = (Map<String, Integer>) foodItemMap.get("macros");
                        foodItemDto.setMacros(foodMacros);

                        foodItems.add(foodItemDto);
                    }

                    mealLogDto.setFoodItems(foodItems);
                }

                mealLogDtos.add(mealLogDto);
            }

            return mealLogDtos;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Map meals from DTO list to JSON
     */
    protected Map<String, Object> mapMealsToJson(List<MealLogDto> meals) {
        if (meals == null) {
            return Map.of();
        }

        return Map.of("meals", meals);
    }

    /**
     * Map workout from JSON to DTO
     */
    protected WorkoutLogDto mapWorkoutFromJson(Map<String, Object> workoutJson) {
        if (workoutJson == null) {
            return null;
        }

        try {
            WorkoutLogDto workoutLogDto = new WorkoutLogDto();

            workoutLogDto.setWorkoutName((String) workoutJson.get("workoutName"));
            workoutLogDto.setDurationMinutes(getIntegerValue(workoutJson, "durationMinutes"));
            workoutLogDto.setCaloriesBurned(getIntegerValue(workoutJson, "caloriesBurned"));
            workoutLogDto.setNotes((String) workoutJson.get("notes"));
            workoutLogDto.setCompleted(getBooleanValue(workoutJson, "completed"));
            workoutLogDto.setPlannedWorkout(getBooleanValue(workoutJson, "plannedWorkout"));

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> exercisesList = (List<Map<String, Object>>) workoutJson.get("exercises");

            if (exercisesList != null) {
                List<ExerciseLogDto> exerciseLogs = new ArrayList<>();

                for (Map<String, Object> exerciseMap : exercisesList) {
                    ExerciseLogDto exerciseLogDto = new ExerciseLogDto();

                    exerciseLogDto.setName((String) exerciseMap.get("name"));
                    exerciseLogDto.setSets(getIntegerValue(exerciseMap, "sets"));
                    exerciseLogDto.setReps((String) exerciseMap.get("reps"));
                    exerciseLogDto.setWeightKg(getIntegerValue(exerciseMap, "weightKg"));
                    exerciseLogDto.setRestSeconds(getIntegerValue(exerciseMap, "restSeconds"));
                    exerciseLogDto.setCompleted(getBooleanValue(exerciseMap, "completed"));

                    exerciseLogs.add(exerciseLogDto);
                }

                workoutLogDto.setExercises(exerciseLogs);
            }

            return workoutLogDto;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Map workout from DTO to JSON
     */
    protected Map<String, Object> mapWorkoutToJson(WorkoutLogDto workout) {
        if (workout == null) {
            return Map.of();
        }

        Map<String, Object> workoutMap = new HashMap<>();

        workoutMap.put("workoutName", workout.getWorkoutName());
        workoutMap.put("durationMinutes", workout.getDurationMinutes());
        workoutMap.put("caloriesBurned", workout.getCaloriesBurned());
        workoutMap.put("notes", workout.getNotes());
        workoutMap.put("completed", workout.getCompleted());
        workoutMap.put("plannedWorkout", workout.getPlannedWorkout());
        workoutMap.put("exercises", workout.getExercises());

        return workoutMap;
    }

    /**
     * Map exercises from JSON to DTO list
     */
    protected List<ExerciseLogDto> mapExercisesFromJson(Map<String, Object> exercisesJson) {
        if (exercisesJson == null) {
            return new ArrayList<>();
        }

        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> exercisesList = (List<Map<String, Object>>) exercisesJson.get("exercises");

            if (exercisesList == null) {
                return new ArrayList<>();
            }

            List<ExerciseLogDto> exerciseLogDtos = new ArrayList<>();

            for (Map<String, Object> exerciseMap : exercisesList) {
                ExerciseLogDto exerciseLogDto = new ExerciseLogDto();

                exerciseLogDto.setName((String) exerciseMap.get("name"));
                exerciseLogDto.setSets(getIntegerValue(exerciseMap, "sets"));
                exerciseLogDto.setReps((String) exerciseMap.get("reps"));
                exerciseLogDto.setWeightKg(getIntegerValue(exerciseMap, "weightKg"));
                exerciseLogDto.setRestSeconds(getIntegerValue(exerciseMap, "restSeconds"));
                exerciseLogDto.setCompleted(getBooleanValue(exerciseMap, "completed"));

                exerciseLogDtos.add(exerciseLogDto);
            }

            return exerciseLogDtos;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Map exercises from DTO list to JSON
     */
    protected Map<String, Object> mapExercisesToJson(List<ExerciseLogDto> exercises) {
        if (exercises == null) {
            return Map.of();
        }

        return Map.of("exercises", exercises);
    }

    /**
     * Helper method to safely get Integer values from a map
     */
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Helper method to safely get Boolean values from a map
     */
    private Boolean getBooleanValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }
}