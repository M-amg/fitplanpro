package com.fitplanpro.dto.tracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for tracking summary
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingSummaryDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer daysTracked;
    private Float weightChange;
    private Float startWeight;
    private Float currentWeight;
    private Integer workoutsCompleted;
    private Integer totalWorkoutMinutes;
    private Double averageWorkoutDuration;
    private Double adherencePercentage;
    private Map<String, Double> measurementChanges;
}
