package com.fitplanpro.repository;

import com.fitplanpro.enums.AchievementCategory;
import com.fitplanpro.entity.AchievementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementTypeRepository extends JpaRepository<AchievementType, Long> {

    /**
     * Find achievement types by category
     *
     * @param category the category to filter by
     * @return a list of achievement types
     */
    List<AchievementType> findByCategory(AchievementCategory category);

    /**
     * Find achievement type by name
     *
     * @param name the name to search for
     * @return an Optional containing the achievement type if found
     */
    Optional<AchievementType> findByName(String name);

    /**
     * Find achievement types by category and points range
     *
     * @param category the category to filter by
     * @param minPoints the minimum points
     * @param maxPoints the maximum points
     * @return a list of achievement types
     */
    List<AchievementType> findByCategoryAndPointsBetween(
            AchievementCategory category, int minPoints, int maxPoints);

    /**
     * Find achievement types sorted by points
     *
     * @return a list of achievement types
     */
    List<AchievementType> findAllByOrderByPointsDesc();

    /**
     * Search achievement types by name or description
     *
     * @param searchTerm the term to search for
     * @return a list of matching achievement types
     */
    @Query("SELECT at FROM AchievementType at WHERE " +
            "LOWER(at.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(at.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<AchievementType> searchAchievements(@Param("searchTerm") String searchTerm);
}

