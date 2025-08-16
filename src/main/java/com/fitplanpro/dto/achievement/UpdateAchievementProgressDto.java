package com.fitplanpro.dto.achievement;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating achievement progress
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAchievementProgressDto {

    @NotNull(message = "Achievement ID is required")
    private Long achievementId;

    @NotNull(message = "Progress is required")
    @Min(value = 1, message = "Progress must be at least 1")
    @Max(value = 100, message = "Progress must be at most 100")
    private Integer progress;
}
