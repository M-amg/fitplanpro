package com.fitplanpro.service;

import com.fitplanpro.dto.achievement.*;
import com.fitplanpro.entity.AchievementType;
import com.fitplanpro.entity.User;
import com.fitplanpro.entity.UserAchievement;
import com.fitplanpro.enums.AchievementCategory;
import com.fitplanpro.exception.AchievementNotFoundException;
import com.fitplanpro.exception.UserNotFoundException;
import com.fitplanpro.mapper.AchievementMapper;
import com.fitplanpro.repository.AchievementTypeRepository;
import com.fitplanpro.repository.UserAchievementRepository;
import com.fitplanpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for achievement operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

    private final AchievementTypeRepository achievementTypeRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;
    private final AchievementMapper achievementMapper;
    private final UserService userService;

    /**
     * Get all achievement types
     *
     * @return list of achievement type DTOs
     */
    @Transactional(readOnly = true)
    public List<AchievementTypeDto> getAllAchievementTypes() {
        List<AchievementType> achievementTypes = achievementTypeRepository.findAll();
        return achievementMapper.toTypeDtoList(achievementTypes);
    }

    /**
     * Get achievement types by category
     *
     * @param category the achievement category
     * @return list of achievement type DTOs
     */
    @Transactional(readOnly = true)
    public List<AchievementTypeDto> getAchievementTypesByCategory(AchievementCategory category) {
        List<AchievementType> achievementTypes = achievementTypeRepository.findByCategory(category);
        return achievementMapper.toTypeDtoList(achievementTypes);
    }

    /**
     * Get user's achievements
     *
     * @return list of user achievement DTOs
     */
    @Transactional(readOnly = true)
    public List<UserAchievementDto> getUserAchievements() {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get user achievements
        List<UserAchievement> userAchievements = userAchievementRepository.findByUser(currentUser);

        return achievementMapper.toUserAchievementDtoList(userAchievements);
    }

    /**
     * Get user's achievements by category
     *
     * @param category the achievement category
     * @return list of user achievement DTOs
     */
    @Transactional(readOnly = true)
    public List<UserAchievementDto> getUserAchievementsByCategory(AchievementCategory category) {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get user achievements by category
        List<UserAchievement> userAchievements = userAchievementRepository.findByUserAndCategory(currentUser, category);

        return achievementMapper.toUserAchievementDtoList(userAchievements);
    }

    /**
     * Get user's achievement summary
     *
     * @return the user achievement summary DTO
     */
    @Transactional(readOnly = true)
    public UserAchievementSummaryDto getUserAchievementSummary() {
        // Get current user
        User currentUser = userService.getCurrentUserEntity();

        // Get user achievements
        List<UserAchievement> userAchievements = userAchievementRepository.findByUser(currentUser);

        // Get total points
        Integer totalPoints = userAchievementRepository.getTotalPointsForUser(currentUser);

        return achievementMapper.toSummaryDto(currentUser, userAchievements, totalPoints);
    }

    /**
     * Award an achievement to a user
     *
     * @param userId the user ID
     * @param achievementName the achievement name
     * @param progress the achievement progress
     * @return true if achievement was awarded or updated successfully
     */
    @Transactional
    public boolean awardAchievement(Long userId, String achievementName, int progress) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Get achievement type
        AchievementType achievementType = achievementTypeRepository.findByName(achievementName)
                .orElseThrow(() -> new AchievementNotFoundException("Achievement not found with name: " + achievementName));

        // Check if user already has this achievement
        Optional<UserAchievement> existingAchievement = userAchievementRepository.findByUserAndAchievementType(user, achievementType);

        if (existingAchievement.isPresent()) {
            // Update progress if higher
            UserAchievement userAchievement = existingAchievement.get();

            if (progress > userAchievement.getProgress()) {
                userAchievement.setProgress(progress);

                // If progress reaches 100%, update achievement date
                if (progress >= 100 && userAchievement.getProgress() < 100) {
                    userAchievement.setAchievedAt(LocalDateTime.now());
                }

                userAchievementRepository.save(userAchievement);
            }
        } else {
            // Create new achievement
            UserAchievement userAchievement = new UserAchievement();
            userAchievement.setUser(user);
            userAchievement.setAchievementType(achievementType);
            userAchievement.setProgress(progress);
            userAchievement.setAchievedAt(progress >= 100 ? LocalDateTime.now() : null);

            userAchievementRepository.save(userAchievement);
        }

        return true;
    }

    /**
     * Grant an achievement to a user
     *
     * @param grantDto the grant achievement DTO
     * @return the user achievement DTO
     */
    @Transactional
    public UserAchievementDto grantAchievement(GrantAchievementDto grantDto) {
        // Get user
        User user = userRepository.findById(grantDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + grantDto.getUserId()));

        // Get achievement type
        AchievementType achievementType = achievementTypeRepository.findById(grantDto.getAchievementTypeId())
                .orElseThrow(() -> new AchievementNotFoundException("Achievement not found with ID: " + grantDto.getAchievementTypeId()));

        // Check if user already has this achievement
        Optional<UserAchievement> existingAchievement = userAchievementRepository.findByUserAndAchievementType(user, achievementType);

        if (existingAchievement.isPresent()) {
            // Update progress if higher
            UserAchievement userAchievement = existingAchievement.get();

            if (grantDto.getProgress() > userAchievement.getProgress()) {
                userAchievement.setProgress(grantDto.getProgress());

                // If progress reaches 100%, update achievement date
                if (grantDto.getProgress() >= 100 && userAchievement.getProgress() < 100) {
                    userAchievement.setAchievedAt(LocalDateTime.now());
                }

                UserAchievement savedAchievement = userAchievementRepository.save(userAchievement);
                return achievementMapper.toUserAchievementDto(savedAchievement);
            } else {
                return achievementMapper.toUserAchievementDto(existingAchievement.get());
            }
        } else {
            // Create new achievement
            UserAchievement userAchievement = achievementMapper.toUserAchievementEntity(grantDto, user, achievementType);
            UserAchievement savedAchievement = userAchievementRepository.save(userAchievement);
            return achievementMapper.toUserAchievementDto(savedAchievement);
        }
    }

    /**
     * Update achievement progress
     *
     * @param updateDto the update progress DTO
     * @return the updated user achievement DTO
     */
    @Transactional
    public UserAchievementDto updateAchievementProgress(UpdateAchievementProgressDto updateDto) {
        // Get achievement
        UserAchievement userAchievement = userAchievementRepository.findById(updateDto.getAchievementId())
                .orElseThrow(() -> new AchievementNotFoundException("User achievement not found with ID: " + updateDto.getAchievementId()));

        // Update progress
        achievementMapper.updateProgress(updateDto, userAchievement);

        // Save updated achievement
        UserAchievement savedAchievement = userAchievementRepository.save(userAchievement);

        return achievementMapper.toUserAchievementDto(savedAchievement);
    }

    /**
     * Create a new achievement type
     *
     * @param createDto the create achievement type DTO
     * @return the created achievement type DTO
     */
    @Transactional
    public AchievementTypeDto createAchievementType(AchievementTypeCreateDto createDto) {
        // Create new achievement type
        AchievementType achievementType = achievementMapper.toTypeEntity(createDto);

        // Save achievement type
        AchievementType savedType = achievementTypeRepository.save(achievementType);

        return achievementMapper.toTypeDto(savedType);
    }
}