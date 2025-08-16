package com.fitplanpro.dto.aiservice;

import com.fitplanpro.enums.GoalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for AI progress analysis request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressAnalysisRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    private GoalType goalType;

    private Map<String, Object> trackingData;

    private Map<String, Object> measurements;
}
