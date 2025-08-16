package com.fitplanpro.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for individual exercise
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private String name;
    private Integer sets;
    private String reps;
    private Integer restSeconds;
    private String notes;
    private String alternativeExercise;
}
