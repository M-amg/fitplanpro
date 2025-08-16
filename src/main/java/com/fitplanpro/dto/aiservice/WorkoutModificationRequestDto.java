package com.fitplanpro.dto.aiservice;

import com.fitplanpro.dto.plan.ExerciseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for AI prompt template
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutModificationRequestDto {

    @NotBlank(message = "Exercise name is required")
    private String exerciseName;

    @NotNull(message = "Original exercise details are required")
    private ExerciseDto originalExercise;

    @NotBlank(message = "Modification reason is required")
    private String modificationReason;

    private String availableEquipment;

    private String injuryConsideration;

    private String experienceLevel;
}

