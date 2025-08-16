package com.fitplanpro.dto.tracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for daily tracking data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyTrackingDto {

    @NotNull(message = "Tracking date is required")
    @PastOrPresent(message = "Tracking date cannot be in the future")
    private LocalDate trackingDate;

    @Min(value = 30, message = "Weight must be at least 30 kg")
    @Max(value = 300, message = "Weight must be at most 300 kg")
    private Float weight;

    private List<MealLogDto> meals;

    private WorkoutLogDto workout;

    @Min(value = 0, message = "Water intake must be at least 0 ml")
    @Max(value = 10000, message = "Water intake must be at most 10000 ml")
    private Integer waterIntake;

    private String progressPhotoUrl;
}

