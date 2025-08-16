package com.fitplanpro.mapper;

import com.fitplanpro.dto.fooddatabase.*;
import com.fitplanpro.dto.plan.ExerciseDto;
import com.fitplanpro.entity.Exercise;
import com.fitplanpro.entity.FoodDatabase;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FoodExerciseMapper {

    /**
     * Convert a list of Exercise entities to a list of ExerciseDatabaseDtos
     *
     * @param exerciseDatabases the list of entities
     * @return the list of DTOs
     */
    List<ExerciseDto> toExerciseDtoList(List<Exercise> exerciseDatabases);

    /**
     * Convert a list of Exercise entities to a list of ExerciseSearchResultDtos
     *
     * @param exerciseDatabases the list of entities
     * @return the list of search result DTOs
     */
    List<ExerciseSearchResultDto> toExerciseSearchResultList(List<Exercise> exerciseDatabases);

    /**
     * Convert FoodDatabase entity to FoodRecommendationDto
     *
     * @param foodDatabase the entity to convert
     * @return the recommendation DTO
     */
    @Mapping(target = "food", source = "foodDatabase")
    @Mapping(target = "recommendationReason", ignore = true)
    @Mapping(target = "matchScore", ignore = true)
    @Mapping(target = "dietaryMatch", ignore = true)
    @Mapping(target = "culturalRelevance", ignore = true)
    @Mapping(target = "nutritionalHighlight", ignore = true)
    FoodRecommendationDto toFoodRecommendation(FoodDatabase foodDatabase);

    /**
     * Convert Exercise entity to ExerciseRecommendationDto
     *
     * @param exerciseDatabase the entity to convert
     * @return the recommendation DTO
     */
    @Mapping(target = "exercise", source = "exerciseDatabase")
    @Mapping(target = "recommendationReason", ignore = true)
    @Mapping(target = "matchScore", ignore = true)
    @Mapping(target = "targetMuscleGroup", ignore = true)
    @Mapping(target = "equipmentMatched", ignore = true)
    @Mapping(target = "difficultyLevel", ignore = true)
    ExerciseRecommendationDto toExerciseRecommendation(Exercise exerciseDatabase);

    /**
     * FoodDatabase entity to FoodDatabaseDto
     *
     * @param foodDatabase the entity to convert
     * @return the DTO
     */
    FoodDatabaseDto toFoodDto(FoodDatabase foodDatabase);

    /**
     * Convert FoodDatabaseCreateDto to FoodDatabase entity
     *
     * @param createDto the DTO to convert
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    FoodDatabase toFoodEntity(FoodDatabaseCreateDto createDto);

    /**
     * Convert FoodDatabase entity to FoodSearchResultDto
     *
     * @param foodDatabase the entity to convert
     * @return the search result DTO
     */
    @Mapping(target = "macroSummary", expression = "java(generateMacroSummary(foodDatabase))")
    FoodSearchResultDto toFoodSearchResult(FoodDatabase foodDatabase);

    /**
     * Generate macro summary string from FoodDatabase entity
     *
     * @param foodDatabase the entity to generate summary for
     * @return the formatted macro summary
     */
    default String generateMacroSummary(FoodDatabase foodDatabase) {
        if (foodDatabase == null) {
            return "";
        }

        return String.format("P: %.1fg, C: %.1fg, F: %.1fg",
                foodDatabase.getProteinPer100g(),
                foodDatabase.getCarbsPer100g(),
                foodDatabase.getFatPer100g());
    }

    /**
     * Convert Exercise entity to ExerciseDatabaseDto
     *
     * @param exerciseDatabase the entity to convert
     * @return the DTO
     */
    ExerciseDto toExerciseDto(Exercise exerciseDatabase);

    /**
     * Convert ExerciseDatabaseCreateDto to Exercise entity
     *
     * @param createDto the DTO to convert
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Exercise toExerciseEntity(ExerciseCreateDto createDto);

    /**
     * Convert Exercise entity to ExerciseSearchResultDto
     *
     * @param exerciseDatabase the entity to convert
     * @return the search result DTO
     */
    @Mapping(target = "equipmentSummary", expression = "java(summarizeEquipment(exerciseDatabase.getEquipmentRequired()))")
    @Mapping(target = "hasVideo", expression = "java(exerciseDatabase.getVideoUrl() != null && !exerciseDatabase.getVideoUrl().isEmpty())")
    ExerciseSearchResultDto toExerciseSearchResult(Exercise exerciseDatabase);

    /**
     * Summarize equipment string
     *
     * @param equipmentRequired the full equipment string
     * @return the summarized equipment string
     */
    default String summarizeEquipment(String equipmentRequired) {
        if (equipmentRequired == null || equipmentRequired.isEmpty()) {
            return "None";
        }

        if (equipmentRequired.toLowerCase().contains("bodyweight") ||
                equipmentRequired.toLowerCase().contains("no equipment") ||
                equipmentRequired.toLowerCase().contains("none")) {
            return "Bodyweight";
        }

        // If the equipment string is too long, truncate it
        if (equipmentRequired.length() > 30) {
            return equipmentRequired.substring(0, 27) + "...";
        }

        return equipmentRequired;
    }

    /**
     * Convert a list of FoodDatabase entities to a list of FoodDatabaseDtos
     *
     * @param foodDatabases the list of entities
     * @return the list of DTOs
     */
    List<FoodDatabaseDto> toFoodDtoList(List<FoodDatabase> foodDatabases);

    /**
     * Convert a list of FoodDatabase entities to a list of FoodSearchResultDtos
     *
     * @param foodDatabases the list of entities
     * @return the list of search result DTOs
     */
    List<FoodSearchResultDto> toFoodSearchResultList(List<FoodDatabase> foodDatabases);

}