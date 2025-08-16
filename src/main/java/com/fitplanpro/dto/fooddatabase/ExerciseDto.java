package com.fitplanpro.dto.fooddatabase;

import com.fitplanpro.enums.ExerciseDifficulty;
import com.fitplanpro.enums.MuscleGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for exercise database item
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private Long id;
    private String name;
    private MuscleGroup muscleGroup;
    private ExerciseDifficulty difficulty;
    private String equipmentRequired;
    private String instructions;
    private String videoUrl;
}
