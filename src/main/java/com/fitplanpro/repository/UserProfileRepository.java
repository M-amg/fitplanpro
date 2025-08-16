package com.fitplanpro.repository;

import com.fitplanpro.enums.GoalType;
import com.fitplanpro.entity.User;
import com.fitplanpro.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Find all profiles for a user
     *
     * @param user the user to find profiles for
     * @return a list of user profiles
     */
    List<UserProfile> findByUser(User user);

    /**
     * Find a profile by its unique hash
     *
     * @param profileHash the hash to search for
     * @return an Optional containing the profile if found
     */
    Optional<UserProfile> findByProfileHash(String profileHash);

    /**
     * Find similar profiles based on key parameters
     *
     * @param gender the gender to match
     * @param age the age to match within ±3 years
     * @param goalType the goal type to match
     * @param currentWeight the weight to match within ±8 kg
     * @param height the height to match within ±8 cm
     * @return list of similar user profiles
     */
    @Query("SELECT up FROM UserProfile up WHERE " +
            "up.gender = :gender AND " +
            "ABS(up.age - :age) <= 3 AND " +
            "up.goalType = :goalType AND " +
            "ABS(up.currentWeight - :currentWeight) <= 8 AND " +
            "ABS(up.height - :height) <= 8")
    List<UserProfile> findSimilarProfiles(
            @Param("gender") String gender,
            @Param("age") Integer age,
            @Param("goalType") GoalType goalType,
            @Param("currentWeight") Float currentWeight,
            @Param("height") Float height);

    /**
     * Find the most recent profile for a user
     *
     * @param user the user to find the profile for
     * @return an Optional containing the most recent profile if found
     */
    Optional<UserProfile> findTopByUserOrderByCreatedAtDesc(User user);

    /**
     * Find profiles matching location and culture
     *
     * @param locationCulture the location/culture to match
     * @return a list of matching profiles
     */
    List<UserProfile> findByLocationCultureContaining(String locationCulture);
}