package com.fitplanpro.repository;

import com.fitplanpro.entity.AchievementType;
import com.fitplanpro.entity.User;
import com.fitplanpro.entity.UserAchievement;
import com.fitplanpro.enums.AchievementCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    /**
     * Find achievements for a user
     *
     * @param user the user to find achievements for
     * @return a list of user achievements
     */
    List<UserAchievement> findByUser(User user);

    /**
     * Find achievements for a user by category
     *
     * @param user     the user to find achievements for
     * @param category the category to filter by
     * @return a list of user achievements
     */
    @Query("SELECT ua FROM UserAchievement ua JOIN ua.achievementType at " +
            "WHERE ua.user = :user AND at.category = :category")
    List<UserAchievement> findByUserAndCategory(
            @Param("user") User user, @Param("category") AchievementCategory category);

    /**
     * Find recent achievements for a user
     *
     * @param user  the user to find achievements for
     * @param limit the maximum number of achievements to return
     * @return a list of recent achievements
     */
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user = :user " +
            "ORDER BY ua.achievedAt DESC")
    List<UserAchievement> findRecentAchievements(@Param("user") User user, @Param("limit") int limit);

    /**
     * Check if a user has a specific achievement
     *
     * @param user            the user to check
     * @param achievementType the achievement type to check for
     * @return true if the user has the achievement
     */
    boolean existsByUserAndAchievementType(User user, AchievementType achievementType);

    /**
     * Count achievements for a user
     *
     * @param user the user to count achievements for
     * @return the count of achievements
     */
    long countByUser(User user);

    /**
     * Get total points for a user
     *
     * @param user the user to calculate points for
     * @return the total points
     */
    @Query("SELECT SUM(at.points) FROM UserAchievement ua JOIN ua.achievementType at " +
            "WHERE ua.user = :user")
    Integer getTotalPointsForUser(@Param("user") User user);

    /**
     * Find achievements earned in date range
     *
     * @param user      the user to find achievements for
     * @param startDate the start date
     * @param endDate   the end date
     * @return a list of achievements
     */
    List<UserAchievement> findByUserAndAchievedAtBetween(
            User user, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find achievements with progress less than 100%
     *
     * @param user the user to find achievements for
     * @return a list of in-progress achievements
     */
    List<UserAchievement> findByUserAndProgressLessThan(User user, int progress);

    /**
     * Find achievements
     *
     * @param user the user to find achievements for
     * @param achievementType the achievementType to find achievements for
     * @return an Optional achievements
     */
    Optional<UserAchievement> findByUserAndAchievementType(User user, AchievementType achievementType);
}
