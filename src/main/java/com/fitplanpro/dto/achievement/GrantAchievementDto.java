package com.fitplanpro.dto.achievement;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for granting an achievement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantAchievementDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Achievement type ID is required")
    private Long achievementTypeId;

    @NotNull(message = "Progress is required")
    @Min(value = 1, message = "Progress must be at least 1")
    @Max(value = 100, message = "Progress must be at most 100")
    private Integer progress;
}
