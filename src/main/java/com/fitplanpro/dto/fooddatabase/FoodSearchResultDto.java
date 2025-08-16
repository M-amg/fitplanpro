package com.fitplanpro.dto.fooddatabase;

import com.fitplanpro.enums.FoodGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for food search result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodSearchResultDto {
    private Long id;
    private String name;
    private String cultureRegion;
    private Float caloriesPer100g;
    private String macroSummary; // e.g. "P: 20g, C: 30g, F: 10g"
    private FoodGroup foodGroup;
    private Boolean isTraditional;
}
