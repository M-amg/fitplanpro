package com.fitplanpro.dto.tracking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for food item in meal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemDto {

    @NotNull(message = "Food name is required")
    private String name;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Unit is required")
    private String unit;

    private Integer calories;

    private Map<String, Integer> macros;
}
