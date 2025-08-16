package com.fitplanpro.dto.aiservice;

import com.fitplanpro.dto.plan.MealDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for AI meal alternatives request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealAlternativesRequestDto {

    @NotBlank(message = "Meal name is required")
    private String mealName;

    @NotNull(message = "Meal details are required")
    private MealDto originalMeal;

    @NotNull(message = "Diet preference is required")
    private String dietPreference;

    private String locationCulture;

    private String foodAllergies;

    private Integer maxCalorieVariation;
}
