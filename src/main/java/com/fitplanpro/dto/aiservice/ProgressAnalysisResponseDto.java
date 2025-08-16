package com.fitplanpro.dto.aiservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for AI progress analysis response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressAnalysisResponseDto {
    private String summary;
    private String goalProgress;
    private Map<String, Object> metrics;
    private Map<String, Object> trends;
    private String nutritionAnalysis;
    private String workoutAnalysis;
    private String recommendations;
}
