package com.fitplanpro.service;

import com.fitplanpro.dto.user.PasswordChangeDto;
import com.fitplanpro.dto.user.UserDto;
import com.fitplanpro.dto.user.UserUpdateDto;
import com.fitplanpro.exception.AuthenticationException;
import com.fitplanpro.exception.UserNotFoundException;
import com.fitplanpro.mapper.UserMapper;
import com.fitplanpro.entity.User;
import com.fitplanpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for user operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get current authenticated user
     *
     * @return the current user entity
     */
    @Transactional(readOnly = true)
    public User getCurrentUserEntity() {
        // Get authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Not authenticated");
        }

        // Get user by email
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /**
     * Get current authenticated user as DTO
     *
     * @return the current user DTO
     */
    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        return userMapper.toDto(getCurrentUserEntity());
    }

    /**
     * Get user by ID
     *
     * @param id the user ID
     * @return the user DTO
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        return userMapper.toDto(user);
    }

    /**
     * Update user information
     *
     * @param updateDto the user update data
     * @return the updated user DTO
     */
    @Transactional
    public UserDto updateUser(UserUpdateDto updateDto) {
        // Get current user
        User currentUser = getCurrentUserEntity();

        // Update user
        userMapper.updateEntity(updateDto, currentUser);

        // Save updated user
        User updatedUser = userRepository.save(currentUser);

        return userMapper.toDto(updatedUser);
    }

    /**
     * Change user password
     *
     * @param passwordChangeDto the password change data
     * @return true if password was changed successfully
     */
    @Transactional
    public boolean changePassword(PasswordChangeDto passwordChangeDto) {
        // Get current user
        User currentUser = getCurrentUserEntity();

        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), currentUser.getPasswordHash())) {
            throw new AuthenticationException("Current password is incorrect");
        }

        // Check if new password and confirm password match
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new AuthenticationException("New password and confirm password do not match");
        }

        // Update password
        currentUser.setPasswordHash(passwordEncoder.encode(passwordChangeDto.getNewPassword()));

        // Save updated user
        userRepository.save(currentUser);

        return true;
    }

    /**
     * Delete user account
     *
     * @return true if account was deleted successfully
     */
    @Transactional
    public boolean deleteAccount() {
        // Get current user
        User currentUser = getCurrentUserEntity();

        // Delete user
        userRepository.delete(currentUser);

        return true;
    }
}