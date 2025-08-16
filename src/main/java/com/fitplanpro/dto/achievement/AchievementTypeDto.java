package com.fitplanpro.dto.achievement;

import com.fitplanpro.enums.AchievementCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for achievement type data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementTypeDto {
    private Long id;
    private String name;
    private String description;
    private AchievementCategory category;
    private String iconUrl;
    private Integer points;
}

