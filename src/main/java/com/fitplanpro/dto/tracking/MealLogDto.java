package com.fitplanpro.dto.tracking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for meal logging
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealLogDto {

    @NotNull(message = "Meal number is required")
    @Min(value = 1, message = "Meal number must be at least 1")
    private Integer mealNumber;

    @NotNull(message = "Meal type is required")
    private String mealType;

    @NotNull(message = "Meal name is required")
    private String name;

    private List<FoodItemDto> foodItems;

    private String photoUrl;

    private Integer totalCalories;

    private Map<String, Integer> macros;

    private Boolean plannedMeal;

    private String notes;
}
