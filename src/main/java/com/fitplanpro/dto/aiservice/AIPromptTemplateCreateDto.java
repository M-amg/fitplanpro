package com.fitplanpro.dto.aiservice;

import com.fitplanpro.enums.PromptTemplateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating AI prompt template
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIPromptTemplateCreateDto {

    @NotBlank(message = "Template name is required")
    private String templateName;

    @NotNull(message = "Template type is required")
    private PromptTemplateType templateType;

    @NotBlank(message = "Template content is required")
    private String templateContent;

    @NotBlank(message = "Version is required")
    private String version;

    private Boolean isActive = true;
}
