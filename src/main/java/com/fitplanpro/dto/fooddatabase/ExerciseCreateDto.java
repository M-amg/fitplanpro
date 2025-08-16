package com.fitplanpro.dto.fooddatabase;

import com.fitplanpro.enums.ExerciseDifficulty;
import com.fitplanpro.enums.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating exercise database item
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCreateDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Muscle group is required")
    private MuscleGroup muscleGroup;

    @NotNull(message = "Difficulty is required")
    private ExerciseDifficulty difficulty;

    @NotBlank(message = "Equipment required is required")
    private String equipmentRequired;

    @NotBlank(message = "Instructions are required")
    private String instructions;

    private String videoUrl;
}
