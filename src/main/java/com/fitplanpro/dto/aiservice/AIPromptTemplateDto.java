package com.fitplanpro.dto.aiservice;

import com.fitplanpro.enums.PromptTemplateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for AI prompt template
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIPromptTemplateDto {
    private Long id;
    private String templateName;
    private PromptTemplateType templateType;
    private String templateContent;
    private String version;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
