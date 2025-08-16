package com.fitplanpro.mapper;


import com.fitplanpro.dto.achievement.*;
import com.fitplanpro.entity.AchievementType;
import com.fitplanpro.entity.User;
import com.fitplanpro.entity.UserAchievement;
import com.fitplanpro.enums.AchievementCategory;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public interface AchievementMapper {

    /**
     * Convert AchievementType entity to AchievementTypeDto
     *
     * @param achievementType the entity to convert
     * @return the DTO
     */
    AchievementTypeDto toTypeDto(AchievementType achievementType);

    /**
     * Convert AchievementTypeCreateDto to AchievementType entity
     *
     * @param createDto the DTO to convert
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AchievementType toTypeEntity(AchievementTypeCreateDto createDto);

    /**
     * Convert UserAchievement entity to UserAchievementDto
     *
     * @param userAchievement the entity to convert
     * @return the DTO
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "achievementType", source = "achievementType")
    UserAchievementDto toUserAchievementDto(UserAchievement userAchievement);

    /**
     * Convert GrantAchievementDto to UserAchievement entity
     *
     * @param grantDto the DTO to convert
     * @param user the user entity
     * @param achievementType the achievement type entity
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "achievementType", source = "achievementType")
    @Mapping(target = "achievedAt", expression = "java(java.time.LocalDateTime.now())")
    UserAchievement toUserAchievementEntity(GrantAchievementDto grantDto, User user, AchievementType achievementType);

    /**
     * Create UserAchievementSummaryDto from user and achievements
     *
     * @param user the user
     * @param achievements the user's achievements
     * @param totalPoints the total points
     * @return the summary DTO
     */
    default UserAchievementSummaryDto toSummaryDto(User user, List<UserAchievement> achievements, Integer totalPoints) {
        if (user == null || achievements == null) {
            return null;
        }

        // Group achievements by category
        Map<String, List<UserAchievement>> achievementsByCategory = achievements.stream()
                .collect(Collectors.groupingBy(
                        ua -> ua.getAchievementType().getCategory().name()
                ));

        // Create category summaries
        List<AchievementCategorySummaryDto> categorySummaries = achievementsByCategory.entrySet().stream()
                .map(entry -> {
                    String category = entry.getKey();
                    List<UserAchievement> categoryAchievements = entry.getValue();

                    int categoryPoints = categoryAchievements.stream()
                            .mapToInt(ua -> ua.getAchievementType().getPoints())
                            .sum();

                    return AchievementCategorySummaryDto.builder()
                            .category(AchievementCategory.valueOf(category))
                            .achievementsCount(categoryAchievements.size())
                            .totalPoints(categoryPoints)
                            .build();
                })
                .toList();

        // Get recent achievements
        List<UserAchievement> recentAchievements = achievements.stream()
                .sorted((a1, a2) -> a2.getAchievedAt().compareTo(a1.getAchievedAt()))
                .limit(5)
                .toList();

        return UserAchievementSummaryDto.builder()
                .userId(user.getId())
                .totalAchievements(achievements.size())
                .totalPoints(totalPoints)
                .categorySummaries(categorySummaries)
                .recentAchievements(toUserAchievementDtoList(recentAchievements))
                .build();
    }

    /**
     * Update UserAchievement entity with UpdateAchievementProgressDto
     *
     * @param updateDto the DTO with updates
     * @param userAchievement the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "achievementType", ignore = true)
    @Mapping(target = "achievedAt", expression = "java(getUpdatedAchievedAt(updateDto, userAchievement))")
    void updateProgress(UpdateAchievementProgressDto updateDto, @MappingTarget UserAchievement userAchievement);

    /**
     * Get updated achievedAt date if progress reaches 100
     *
     * @param updateDto the update DTO
     * @param userAchievement the current achievement entity
     * @return the updated achievedAt date
     */
    default LocalDateTime getUpdatedAchievedAt(UpdateAchievementProgressDto updateDto, UserAchievement userAchievement) {
        if (userAchievement.getProgress() < 100 && updateDto.getProgress() >= 100) {
            return LocalDateTime.now();
        }
        return userAchievement.getAchievedAt();
    }

    /**
     * Convert a list of AchievementType entities to a list of AchievementTypeDtos
     *
     * @param achievementTypes the list of entities
     * @return the list of DTOs
     */
    List<AchievementTypeDto> toTypeDtoList(List<AchievementType> achievementTypes);

    /**
     * Convert a list of UserAchievement entities to a list of UserAchievementDtos
     *
     * @param userAchievements the list of entities
     * @return the list of DTOs
     */
    List<UserAchievementDto> toUserAchievementDtoList(List<UserAchievement> userAchievements);
}