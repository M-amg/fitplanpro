package com.fitplanpro.dto.aiservice;

import com.fitplanpro.enums.PromptTemplateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * DTO for AI service request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIServiceRequestDto {

    @NotNull(message = "Template type is required")
    private PromptTemplateType templateType;

    @NotNull(message = "Parameters are required")
    private Map<String, Object> parameters;

    private String modelPreference;

    private Integer maxTokens;

    private Double temperature;
}

