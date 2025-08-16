package com.fitplanpro.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for daily workout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyWorkoutDto {
    private Integer day;
    private String focus;
    private List<ExerciseDto> exercises;
    private Integer estimatedDuration;
    private String warmup;
    private String cooldown;
}
