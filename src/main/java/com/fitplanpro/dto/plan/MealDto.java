package com.fitplanpro.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for individual meal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealDto {
    private Integer mealNumber;
    private String name;
    private List<String> ingredients;
    private String preparation;
    private Integer calories;
    private Map<String, Integer> macros;
}
