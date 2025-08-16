package com.fitplanpro.dto.plan;

import com.fitplanpro.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for basic plan information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanSummaryDto {
    private Long id;
    private PlanType planType;
    private String profileHash;
    private LocalDateTime generationTime;
    private LocalDateTime expiryTime;
    private Float similarityScore;
    private String aiModelUsed;
}
