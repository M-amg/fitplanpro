package com.fitplanpro.repository;

import com.fitplanpro.entity.ProgressPhoto;
import com.fitplanpro.entity.User;
import com.fitplanpro.enums.PhotoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressPhotoRepository extends JpaRepository<ProgressPhoto, Long> {

    /**
     * Find photos for a user
     *
     * @param user the user to find photos for
     * @return a list of photos
     */
    List<ProgressPhoto> findByUserOrderByPhotoDateDesc(User user);

    /**
     * Find photos for a user by type
     *
     * @param user      the user to find photos for
     * @param photoType the photo type to filter by
     * @return a list of photos
     */
    List<ProgressPhoto> findByUserAndPhotoTypeOrderByPhotoDateDesc(User user, PhotoType photoType);

    /**
     * Find photos for a user between two dates
     *
     * @param user      the user to find photos for
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return a list of photos
     */
    List<ProgressPhoto> findByUserAndPhotoDateBetweenOrderByPhotoDate(
            User user, LocalDate startDate, LocalDate endDate);

    /**
     * Find the most recent photo of a specific type for a user
     *
     * @param user      the user to find photos for
     * @param photoType the photo type to filter by
     * @return an Optional containing the most recent photo if found
     */
    Optional<ProgressPhoto> findTopByUserAndPhotoTypeOrderByPhotoDateDesc(User user, PhotoType photoType);

    /**
     * Count photos by user and type
     *
     * @param user      the user to count photos for
     * @param photoType the photo type to filter by
     * @return the count of photos
     */
    long countByUserAndPhotoType(User user, PhotoType photoType);
}
