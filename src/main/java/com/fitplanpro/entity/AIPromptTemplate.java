package com.fitplanpro.entity;

import com.fitplanpro.enums.PromptTemplateType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_prompt_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIPromptTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name", nullable = false, unique = true)
    private String templateName;

    @Column(name = "template_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PromptTemplateType templateType;

    @Column(name = "template_content", nullable = false, columnDefinition = "text")
    private String templateContent;

    @Column(nullable = false)
    private String version;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}