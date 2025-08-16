package com.fitplanpro.controller;

import com.fitplanpro.dto.common.ApiResponseDto;
import com.fitplanpro.dto.common.ErrorResponseDto;
import com.fitplanpro.dto.user.PasswordChangeDto;
import com.fitplanpro.dto.user.UserDto;
import com.fitplanpro.dto.user.UserUpdateDto;
import com.fitplanpro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user operations
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Get current user information
     *
     * @return user DTO
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieves information about the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserDto>> getCurrentUser() {
        UserDto userDto = userService.getCurrentUser();

        ApiResponseDto<UserDto> response = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(userDto)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Update user information
     *
     * @param updateDto user update DTO
     * @return updated user DTO
     */
    @PutMapping("/me")
    @Operation(summary = "Update user", description = "Updates information for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserDto>> updateUser(
            @RequestBody @Valid UserUpdateDto updateDto) {
        UserDto updatedUser = userService.updateUser(updateDto);

        ApiResponseDto<UserDto> response = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("User updated successfully")
                .data(updatedUser)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Change user password
     *
     * @param passwordChangeDto password change DTO
     * @return success response
     */
    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Changes the password for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized or incorrect current password",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<Void>> changePassword(
            @RequestBody @Valid PasswordChangeDto passwordChangeDto) {
        boolean success = userService.changePassword(passwordChangeDto);

        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .success(success)
                .message("Password changed successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Delete user account
     *
     * @return success response
     */
    @DeleteMapping("/me")
    @Operation(summary = "Delete account", description = "Deletes the authenticated user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<Void>> deleteAccount() {
        boolean success = userService.deleteAccount();

        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .success(success)
                .message("Account deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID (admin only)
     *
     * @param id user ID
     * @return user DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves user information by ID (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserDto>> getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);

        ApiResponseDto<UserDto> response = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(userDto)
                .build();

        return ResponseEntity.ok(response);
    }
}