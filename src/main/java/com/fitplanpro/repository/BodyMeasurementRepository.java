package com.fitplanpro.repository;

import com.fitplanpro.entity.BodyMeasurement;
import com.fitplanpro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, Long> {

    /**
     * Find measurements for a user on a specific date
     *
     * @param user the user to find measurements for
     * @param measurementDate the date to search for
     * @return an Optional containing the measurement if found
     */
    Optional<BodyMeasurement> findByUserAndMeasurementDate(User user, LocalDate measurementDate);

    /**
     * Find measurements for a user
     *
     * @param user the user to find measurements for
     * @return a list of measurements
     */
    List<BodyMeasurement> findByUserOrderByMeasurementDateDesc(User user);

    /**
     * Find measurements for a user between two dates
     *
     * @param user the user to find measurements for
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of measurements
     */
    List<BodyMeasurement> findByUserAndMeasurementDateBetweenOrderByMeasurementDate(
            User user, LocalDate startDate, LocalDate endDate);

    /**
     * Find the most recent measurement for a user
     *
     * @param user the user to find measurements for
     * @return an Optional containing the most recent measurement if found
     */
    Optional<BodyMeasurement> findTopByUserOrderByMeasurementDateDesc(User user);

    /**
     * Calculate waist-to-hip ratio trend for a user
     *
     * @param user the user to calculate for
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of date and ratio pairs
     */
    @Query("SELECT NEW map(bm.measurementDate as date, (bm.waist / bm.hips) as ratio) " +
            "FROM BodyMeasurement bm " +
            "WHERE bm.user = :user " +
            "AND bm.measurementDate BETWEEN :startDate AND :endDate " +
            "AND bm.waist IS NOT NULL AND bm.hips IS NOT NULL " +
            "ORDER BY bm.measurementDate")
    List<Object> calculateWaistToHipRatioTrend(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

