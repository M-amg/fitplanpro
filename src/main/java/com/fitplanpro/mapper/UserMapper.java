package com.fitplanpro.mapper;

import com.fitplanpro.dto.user.UserDto;
import com.fitplanpro.dto.user.UserRegistrationDto;
import com.fitplanpro.dto.user.UserUpdateDto;
import com.fitplanpro.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Convert User entity to UserDto
     *
     * @param user the entity to convert
     * @return the converted DTO
     */
    UserDto toDto(User user);

    /**
     * Convert UserRegistrationDto to User entity
     * Note: password should be encoded before saving
     *
     * @param registrationDto the registration DTO
     * @return the converted entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    User toEntity(UserRegistrationDto registrationDto);

    /**
     * Update User entity with UserUpdateDto
     *
     * @param updateDto the update DTO
     * @param user the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    void updateEntity(UserUpdateDto updateDto, @MappingTarget User user);

    /**
     * Convert a list of User entities to a list of UserDtos
     *
     * @param users the list of entities
     * @return the list of DTOs
     */
    java.util.List<UserDto> toDtoList(java.util.List<User> users);
}