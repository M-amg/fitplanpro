package com.fitplanpro.dto.achievement;

import com.fitplanpro.enums.AchievementCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for achievement category summary
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementCategorySummaryDto {
    private AchievementCategory category;
    private Integer achievementsCount;
    private Integer totalPoints;
}
