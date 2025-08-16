package com.fitplanpro.dto.fooddatabase;

import com.fitplanpro.enums.FoodGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating food database item
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDatabaseCreateDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Culture/region is required")
    private String cultureRegion;

    @NotNull(message = "Calories are required")
    @Min(value = 0, message = "Calories must be at least 0")
    @Max(value = 1000, message = "Calories must be at most 1000")
    private Float caloriesPer100g;

    @NotNull(message = "Protein is required")
    @Min(value = 0, message = "Protein must be at least 0")
    @Max(value = 100, message = "Protein must be at most 100")
    private Float proteinPer100g;

    @NotNull(message = "Carbs are required")
    @Min(value = 0, message = "Carbs must be at least 0")
    @Max(value = 100, message = "Carbs must be at most 100")
    private Float carbsPer100g;

    @NotNull(message = "Fat is required")
    @Min(value = 0, message = "Fat must be at least 0")
    @Max(value = 100, message = "Fat must be at most 100")
    private Float fatPer100g;

    @NotNull(message = "Food group is required")
    private FoodGroup foodGroup;

    private String seasonalAvailability;

    private Boolean isTraditional = false;
}
