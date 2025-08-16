package com.fitplanpro.dto.plan;

import com.fitplanpro.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for detailed workout plan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanDto {
    private Long id;
    private PlanType planType;
    private String profileHash;
    private List<DailyWorkoutDto> weeklySchedule;
    private String focus;
    private String recommendedEquipment;
    private LocalDateTime generationTime;
    private LocalDateTime expiryTime;
}
