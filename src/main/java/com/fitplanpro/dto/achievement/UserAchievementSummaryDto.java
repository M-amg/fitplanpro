package com.fitplanpro.dto.achievement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for user achievement summary
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievementSummaryDto {
    private Long userId;
    private Integer totalAchievements;
    private Integer totalPoints;
    private List<AchievementCategorySummaryDto> categorySummaries;
    private List<UserAchievementDto> recentAchievements;
}
