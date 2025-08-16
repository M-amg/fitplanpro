package com.fitplanpro.dto.tracking;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for exercise in workout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLogDto {

    @NotNull(message = "Exercise name is required")
    private String name;

    @NotNull(message = "Sets are required")
    @Min(value = 1, message = "Sets must be at least 1")
    @Max(value = 20, message = "Sets must be at most 20")
    private Integer sets;

    @NotNull(message = "Reps are required")
    private String reps;

    private Integer weightKg;

    private Integer restSeconds;

    private Boolean completed;
}
