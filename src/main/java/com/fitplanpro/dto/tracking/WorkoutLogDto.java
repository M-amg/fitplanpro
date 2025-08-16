package com.fitplanpro.dto.tracking;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for workout logging
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogDto {

    @NotNull(message = "Workout name is required")
    private String workoutName;

    @NotNull(message = "Duration is required")
    @Min(value = 5, message = "Duration must be at least 5 minutes")
    @Max(value = 300, message = "Duration must be at most 300 minutes")
    private Integer durationMinutes;

    private Integer caloriesBurned;

    private List<ExerciseLogDto> exercises;

    private String notes;

    private Boolean completed;

    private Boolean plannedWorkout;
}
