package com.fitplanpro.dto.tracking;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for body measurements
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurementDto {

    @NotNull(message = "Measurement date is required")
    @PastOrPresent(message = "Measurement date cannot be in the future")
    private LocalDate measurementDate;

    @Min(value = 30, message = "Chest must be at least 30 cm")
    @Max(value = 200, message = "Chest must be at most 200 cm")
    private Float chest;

    @Min(value = 30, message = "Waist must be at least 30 cm")
    @Max(value = 200, message = "Waist must be at most 200 cm")
    private Float waist;

    @Min(value = 30, message = "Hips must be at least 30 cm")
    @Max(value = 200, message = "Hips must be at most 200 cm")
    private Float hips;

    @Min(value = 15, message = "Arms must be at least 15 cm")
    @Max(value = 100, message = "Arms must be at most 100 cm")
    private Float arms;

    @Min(value = 20, message = "Thighs must be at least 20 cm")
    @Max(value = 150, message = "Thighs must be at most 150 cm")
    private Float thighs;
}
