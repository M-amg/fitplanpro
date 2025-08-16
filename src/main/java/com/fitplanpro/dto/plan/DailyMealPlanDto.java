package com.fitplanpro.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for daily meal plan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyMealPlanDto {
    private Integer day;
    private List<MealDto> meals;
    private Integer totalCalories;
    private MacroSplitDto totalMacros;
}
