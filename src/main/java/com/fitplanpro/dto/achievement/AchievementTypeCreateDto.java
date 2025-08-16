package com.fitplanpro.dto.achievement;

import com.fitplanpro.enums.AchievementCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating achievement type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementTypeCreateDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private AchievementCategory category;

    private String iconUrl;

    @NotNull(message = "Points are required")
    @Min(value = 5, message = "Points must be at least 5")
    @Max(value = 100, message = "Points must be at most 100")
    private Integer points;
}
