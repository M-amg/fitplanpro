package com.fitplanpro.dto.fooddatabase;

import com.fitplanpro.enums.FoodGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for food database item
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDatabaseDto {
    private Long id;
    private String name;
    private String cultureRegion;
    private Float caloriesPer100g;
    private Float proteinPer100g;
    private Float carbsPer100g;
    private Float fatPer100g;
    private FoodGroup foodGroup;
    private String seasonalAvailability;
    private Boolean isTraditional;
}

