package com.fitplanpro.repository;

import com.fitplanpro.entity.Tracking;
import com.fitplanpro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Long> {

    /**
     * Find tracking data for a user on a specific date
     *
     * @param user the user to find tracking data for
     * @param trackingDate the date to search for
     * @return an Optional containing the tracking data if found
     */
    Optional<Tracking> findByUserAndTrackingDate(User user, LocalDate trackingDate);

    /**
     * Find all tracking data for a user
     *
     * @param user the user to find tracking data for
     * @return a list of tracking data
     */
    List<Tracking> findByUser(User user);

    /**
     * Find tracking data for a user between two dates
     *
     * @param user the user to find tracking data for
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of tracking data
     */
    List<Tracking> findByUserAndTrackingDateBetweenOrderByTrackingDate(
            User user, LocalDate startDate, LocalDate endDate);

    /**
     * Find recent tracking data for a user
     *
     * @param userId the user to find tracking data for
     * @param limit the maximum number of records to return
     * @return a list of recent tracking data
     */
    @Query(value = "SELECT * FROM tracking_data " +
            "WHERE user_id = :userId " +
            "ORDER BY tracking_date DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Tracking> findRecentTracking(
            @Param("userId") Long userId,
            @Param("limit") int limit);

    /**
     * Calculate average weight for a user between two dates
     *
     * @param user the user to calculate for
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the average weight
     */
    @Query("SELECT AVG(td.weight) FROM Tracking td " +
            "WHERE td.user = :user " +
            "AND td.trackingDate BETWEEN :startDate AND :endDate " +
            "AND td.weight IS NOT NULL")
    Double calculateAverageWeight(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Count tracking entries for a user by date
     *
     * @param user the user to count for
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the count of tracking entries
     */
    long countByUserAndTrackingDateBetween(User user, LocalDate startDate, LocalDate endDate);

    /**
     * Check if a user has tracked consistently in the past week
     *
     * @param user the user to check
     * @param endDate the end date (today)
     * @param startDate the start date (7 days ago)
     * @return true if the user has tracked at least 5 days in the past week
     */
    @Query("SELECT COUNT(DISTINCT td.trackingDate) >= 5 FROM Tracking td " +
            "WHERE td.user = :user " +
            "AND td.trackingDate BETWEEN :startDate AND :endDate")
    boolean hasTrackedConsistently(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}