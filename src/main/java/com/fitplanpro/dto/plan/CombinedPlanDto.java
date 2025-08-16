package com.fitplanpro.dto.plan;

import com.fitplanpro.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for combined plan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedPlanDto {
    private Long id;
    private PlanType planType;
    private String profileHash;
    private MealPlanDto mealPlan;
    private WorkoutPlanDto workoutPlan;
    private Map<String, Object> recommendations;
    private LocalDateTime generationTime;
    private LocalDateTime expiryTime;
}
