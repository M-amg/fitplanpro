package com.fitplanpro.dto.plan;

import com.fitplanpro.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for detailed meal plan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanDto {
    private Long id;
    private PlanType planType;
    private String profileHash;
    private Integer dailyCalories;
    private MacroSplitDto macros;
    private List<DailyMealPlanDto> dailyPlans;
    private LocalDateTime generationTime;
    private LocalDateTime expiryTime;
}
