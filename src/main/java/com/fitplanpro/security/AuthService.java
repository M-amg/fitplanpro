package com.fitplanpro.security;

import com.fitplanpro.dto.user.AuthResponseDto;
import com.fitplanpro.dto.user.UserDto;
import com.fitplanpro.dto.user.UserLoginDto;
import com.fitplanpro.dto.user.UserRegistrationDto;
import com.fitplanpro.entity.User;
import com.fitplanpro.exception.AuthenticationException;
import com.fitplanpro.exception.UserAlreadyExistsException;
import com.fitplanpro.mapper.UserMapper;
import com.fitplanpro.repository.UserProfileRepository;
import com.fitplanpro.repository.UserRepository;
import com.fitplanpro.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for user authentication operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user
     *
     * @param registrationDto the registration data
     * @return authentication response with token and user data
     */
    @Transactional
    public AuthResponseDto register(UserRegistrationDto registrationDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + registrationDto.getEmail() + " already exists");
        }

        if (registrationDto.getPhone() != null && userRepository.existsByPhone(registrationDto.getPhone())) {
            throw new UserAlreadyExistsException("User with phone " + registrationDto.getPhone() + " already exists");
        }

        // Create new user
        User user = userMapper.toEntity(registrationDto);

        // Encode password
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));

        // Set created date
        user.setCreatedAt(LocalDateTime.now());

        // Save user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtTokenProvider.createToken(savedUser.getEmail());

        // Convert to DTO
        UserDto userDto = userMapper.toDto(savedUser);

        // Check if user has a profile
        boolean hasProfile = userProfileRepository.findTopByUserOrderByCreatedAtDesc(savedUser).isPresent();

        // Return authentication response
        return AuthResponseDto.builder()
                .token(token)
                .user(userDto)
                .hasProfile(hasProfile)
                .build();
    }

    /**
     * Login a user
     *
     * @param loginDto the login data
     * @return authentication response with token and user data
     */
    @Transactional
    public AuthResponseDto login(UserLoginDto loginDto) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmailOrPhone(), loginDto.getPassword())
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            // Update last login time
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Generate JWT token
            String token = jwtTokenProvider.createToken(email);

            // Convert to DTO
            UserDto userDto = userMapper.toDto(user);

            // Check if user has a profile
            boolean hasProfile = userProfileRepository.findTopByUserOrderByCreatedAtDesc(user).isPresent();

            // Return authentication response
            return AuthResponseDto.builder()
                    .token(token)
                    .user(userDto)
                    .hasProfile(hasProfile)
                    .build();

        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new AuthenticationException("Invalid email/phone or password");
        }
    }

    /**
     * Get current authenticated user
     *
     * @return the current user
     */
    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        // Get authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Not authenticated");
        }

        // Get user by email
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        // Convert to DTO
        return userMapper.toDto(user);
    }
}