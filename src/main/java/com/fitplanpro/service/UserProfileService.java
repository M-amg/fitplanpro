package com.fitplanpro.service;

import com.fitplanpro.dto.profile.ProfileMetricsDto;
import com.fitplanpro.dto.profile.UserProfileCreateDto;
import com.fitplanpro.dto.profile.UserProfileDto;
import com.fitplanpro.dto.profile.UserProfileUpdateDto;
import com.fitplanpro.exception.ProfileNotFoundException;
import com.fitplanpro.mapper.UserProfileMapper;
import com.fitplanpro.entity.User;
import com.fitplanpro.entity.UserProfile;
import com.fitplanpro.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for user profile operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserService userService;

    /**
     * Create a new user profile
     *
     * @param createDto the profile creation data
     * @return the created profile DTO
     */
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#result.userId")
    public UserProfileDto createProfile(UserProfileCreateDto createDto) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Create new profile
        UserProfile userProfile = userProfileMapper.toEntity(createDto, currentUser);

        // Generate profile hash
        userProfileMapper.generateProfileHash(userProfile);

        // Save profile
        UserProfile savedProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.toDto(savedProfile);
    }

    /**
     * Get current user's profile
     *
     * @return the user profile DTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "userProfiles", key = "#result.userId")
    public UserProfileDto getCurrentUserProfile() {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get latest profile
        UserProfile userProfile = userProfileRepository.findTopByUserOrderByCreatedAtDesc(currentUser)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for current user"));

        return userProfileMapper.toDto(userProfile);
    }

    /**
     * Get profile by ID
     *
     * @param id the profile ID
     * @return the profile DTO
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "userProfiles", key = "#id")
    public UserProfileDto getProfileById(Long id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID: " + id));

        return userProfileMapper.toDto(userProfile);
    }

    /**
     * Update user profile
     *
     * @param updateDto the profile update data
     * @return the updated profile DTO
     */
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#result.userId")
    public UserProfileDto updateProfile(UserProfileUpdateDto updateDto) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get latest profile
        UserProfile userProfile = userProfileRepository.findTopByUserOrderByCreatedAtDesc(currentUser)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for current user"));

        // Update profile
        userProfileMapper.updateEntity(updateDto, userProfile);

        // Re-generate profile hash if critical fields have changed
        if (updateDto.getCurrentWeight() != null ||
                updateDto.getGoalType() != null ||
                updateDto.getTargetWeight() != null) {
            userProfileMapper.generateProfileHash(userProfile);
        }

        // Save updated profile
        UserProfile updatedProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.toDto(updatedProfile);
    }

    /**
     * Get user's profile metrics
     *
     * @return the profile metrics DTO
     */
    @Transactional(readOnly = true)
    public ProfileMetricsDto getProfileMetrics() {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get latest profile
        UserProfile userProfile = userProfileRepository.findTopByUserOrderByCreatedAtDesc(currentUser)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for current user"));

        return userProfileMapper.toMetricsDto(userProfile);
    }

    /**
     * Find similar profiles for AI recommendations
     *
     * @param profileId the profile ID to find similar profiles for
     * @param limit the maximum number of similar profiles to return
     * @return list of similar profile DTOs
     */
    @Transactional(readOnly = true)
    public List<UserProfileDto> findSimilarProfiles(Long profileId, int limit) {
        // Get profile
        UserProfile userProfile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID: " + profileId));

        // Find similar profiles
        List<UserProfile> similarProfiles = userProfileRepository.findSimilarProfiles(
                userProfile.getGender().toString(),
                userProfile.getAge(),
                userProfile.getGoalType(),
                userProfile.getCurrentWeight(),
                userProfile.getHeight());

        // Limit results
        List<UserProfile> limitedProfiles = similarProfiles.stream()
                .filter(profile -> !profile.getId().equals(profileId)) // Exclude current profile
                .limit(limit)
                .toList();

        return userProfileMapper.toDtoList(limitedProfiles);
    }

    /**
     * Check if user has a profile
     *
     * @return true if user has a profile
     */
    @Transactional(readOnly = true)
    public boolean hasProfile() {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Check if user has a profile
        Optional<UserProfile> profile = userProfileRepository.findTopByUserOrderByCreatedAtDesc(currentUser);

        return profile.isPresent();
    }
}