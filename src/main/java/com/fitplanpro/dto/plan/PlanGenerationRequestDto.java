package com.fitplanpro.dto.plan;

import com.fitplanpro.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for plan generation request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanGenerationRequestDto {
    private PlanType planType;
    private Long profileId;
    private Boolean forceRegenerate;
}

