package com.fitplanpro.dto.aiservice;

import com.fitplanpro.enums.PromptTemplateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for AI usage metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIUsageMetricsDto {
    private Long totalRequests;
    private Long cachedResponses;
    private Long directResponses;
    private Double cacheHitRatio;
    private Map<String, Long> requestsByModel;
    private Map<PromptTemplateType, Long> requestsByTemplateType;
    private Double averageResponseTimeMs;
    private Long tokensConsumed;
    private Double estimatedCost;
}
