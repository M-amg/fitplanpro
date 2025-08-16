package com.fitplanpro.repository;

import com.fitplanpro.entity.User;
import com.fitplanpro.entity.WorkoutHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutHistoryRepository extends JpaRepository<WorkoutHistory, Long> {

    /**
     * Find workout history for a user
     *
     * @param user the user to find workout history for
     * @return a list of workout history entries
     */
    List<WorkoutHistory> findByUserOrderByWorkoutDateDesc(User user);

    /**
     * Find workout history for a user between two dates
     *
     * @param user      the user to find workout history for
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return a list of workout history entries
     */
    List<WorkoutHistory> findByUserAndWorkoutDateBetweenOrderByWorkoutDate(
            User user, LocalDate startDate, LocalDate endDate);

    /**
     * Find the most recent workout for a user
     *
     * @param user the user to find workout history for
     * @return an Optional containing the most recent workout if found
     */
    Optional<WorkoutHistory> findTopByUserOrderByWorkoutDateDesc(User user);

    /**
     * Count workouts by user in date range
     *
     * @param user      the user to count workouts for
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return the count of workouts
     */
    long countByUserAndWorkoutDateBetween(User user, LocalDate startDate, LocalDate endDate);

    /**
     * Count workouts by user
     *
     * @param user      the user to count workouts for
     * @return the count of workouts
     */
    long countByUser(User user);

    /**
     * Calculate average workout duration for a user
     *
     * @param user      the user to calculate for
     * @param startDate the start date
     * @param endDate   the end date
     * @return the average duration in minutes
     */
    @Query("SELECT AVG(wh.durationMinutes) FROM WorkoutHistory wh " +
            "WHERE wh.user = :user " +
            "AND wh.workoutDate BETWEEN :startDate AND :endDate")
    Double calculateAverageWorkoutDuration(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Calculate total calories burned by a user
     *
     * @param user      the user to calculate for
     * @param startDate the start date
     * @param endDate   the end date
     * @return the total calories burned
     */
    @Query("SELECT SUM(wh.caloriesBurned) FROM WorkoutHistory wh " +
            "WHERE wh.user = :user " +
            "AND wh.workoutDate BETWEEN :startDate AND :endDate " +
            "AND wh.caloriesBurned IS NOT NULL")
    Integer calculateTotalCaloriesBurned(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find most frequent workout by name
     *
     * @param userId      the user to find for
     * @param startDate the start date
     * @param endDate   the end date
     * @return the most frequent workout name and count
     */
    @Query(value = "SELECT workout_name, COUNT(*) as count FROM workout_history " +
            "WHERE user_id = :userId " +
            "AND workout_date BETWEEN :startDate AND :endDate " +
            "GROUP BY workout_name " +
            "ORDER BY count DESC " +
            "LIMIT 1", nativeQuery = true)
    Object findMostFrequentWorkout(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
