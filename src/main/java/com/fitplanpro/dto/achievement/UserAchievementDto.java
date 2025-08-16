package com.fitplanpro.dto.achievement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user achievement data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievementDto {
    private Long id;
    private Long userId;
    private AchievementTypeDto achievementType;
    private LocalDateTime achievedAt;
    private Integer progress;
}
