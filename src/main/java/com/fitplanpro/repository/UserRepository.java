package com.fitplanpro.repository;

import com.fitplanpro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by email
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by phone number
     *
     * @param phone the phone number to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByPhone(String phone);

    /**
     * Check if a user exists with the given email
     *
     * @param email the email to check
     * @return true if a user exists with this email
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists with the given phone number
     *
     * @param phone the phone number to check
     * @return true if a user exists with this phone number
     */
    boolean existsByPhone(String phone);
}