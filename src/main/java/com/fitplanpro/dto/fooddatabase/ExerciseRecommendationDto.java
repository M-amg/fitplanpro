package com.fitplanpro.dto.fooddatabase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for exercise recommendation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRecommendationDto {
    private ExerciseDto exercise;
    private String recommendationReason;
    private Float matchScore;
    private String targetMuscleGroup;
    private String equipmentMatched;
    private String difficultyLevel;
}
