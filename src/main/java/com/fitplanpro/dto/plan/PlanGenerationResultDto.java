package com.fitplanpro.dto.plan;

import com.fitplanpro.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for AI plan generation result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanGenerationResultDto {
    private Long planId;
    private PlanType planType;
    private Boolean fromCache;
    private Float similarityScore;
    private String aiModelUsed;
    private Long generationTimeMs;
}
