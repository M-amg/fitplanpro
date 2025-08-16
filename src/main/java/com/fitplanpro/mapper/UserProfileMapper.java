package com.fitplanpro.mapper;


import com.fitplanpro.dto.profile.ProfileMetricsDto;
import com.fitplanpro.dto.profile.UserProfileCreateDto;
import com.fitplanpro.dto.profile.UserProfileDto;
import com.fitplanpro.dto.profile.UserProfileUpdateDto;
import com.fitplanpro.entity.User;
import com.fitplanpro.entity.UserProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public interface UserProfileMapper {

    /**
     * Convert UserProfile entity to UserProfileDto
     *
     * @param userProfile the entity to convert
     * @return the converted DTO
     */
    @Mapping(target = "userId", source = "user.id")
    UserProfileDto toDto(UserProfile userProfile);

    /**
     * Convert UserProfileCreateDto to UserProfile entity
     *
     * @param createDto the create DTO
     * @param user the user entity to associate with the profile
     * @return the converted entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "profileHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserProfile toEntity(UserProfileCreateDto createDto, User user);

    /**
     * Update UserProfile entity with UserProfileUpdateDto
     *
     * @param updateDto the update DTO
     * @param userProfile the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "height", ignore = true)
    @Mapping(target = "locationCulture", ignore = true)
    @Mapping(target = "profileHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UserProfileUpdateDto updateDto, @MappingTarget UserProfile userProfile);

    /**
     * Convert UserProfile entity to ProfileMetricsDto
     *
     * @param userProfile the entity to convert
     * @return the simplified metrics DTO
     */
    ProfileMetricsDto toMetricsDto(UserProfile userProfile);

    /**
     * Convert a list of UserProfile entities to a list of UserProfileDtos
     *
     * @param userProfiles the list of entities
     * @return the list of DTOs
     */
    java.util.List<UserProfileDto> toDtoList(java.util.List<UserProfile> userProfiles);

    /**
     * Create a mapping expression to generate a profile hash
     *
     * @param userProfile the profile to generate a hash for
     * @return the hash string
     */
    @AfterMapping
    default void generateProfileHash(@MappingTarget UserProfile userProfile) {
        // Simple hash generation example - in production, use a more robust approach
        if (userProfile.getProfileHash() == null && userProfile.getUser() != null) {
            String hashInput = userProfile.getUser().getId() + ":" +
                    userProfile.getGender() + ":" +
                    userProfile.getAge() + ":" +
                    userProfile.getHeight() + ":" +
                    userProfile.getCurrentWeight() + ":" +
                    userProfile.getGoalType();

            userProfile.setProfileHash(String.valueOf(hashInput.hashCode()));
        }
    }
}