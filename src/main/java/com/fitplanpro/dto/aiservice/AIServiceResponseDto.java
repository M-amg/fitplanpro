package com.fitplanpro.dto.aiservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for AI service response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIServiceResponseDto {
    private String modelUsed;
    private Map<String, Object> response;
    private Long processingTimeMs;
    private Boolean fromCache;
    private String cacheKey;
}
