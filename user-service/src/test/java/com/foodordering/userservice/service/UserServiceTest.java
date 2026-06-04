package com.foodordering.userservice.service;

import com.foodordering.userservice.dto.LoginRequest;
import com.foodordering.userservice.dto.RegisterRequest;
import com.foodordering.userservice.dto.AuthResponse;
import com.foodordering.userservice.model.User;
import com.foodordering.userservice.repository.UserRepository;
import com.foodordering.userservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("denis");
        registerRequest.setEmail("denis@email.com");
        registerRequest.setPassword("secret123");

        savedUser = User.builder()
                .id(1L)
                .username("denis")
                .email("denis@email.com")
                .password("hashed_password")
                .role(User.Role.CUSTOMER)
                .build();
    }

    @Test
    void register_shouldReturnToken_whenValidRequest() {
        when(userRepository.existsByUsername("denis")).thenReturn(false);
        when(userRepository.existsByEmail("denis@email.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDetails mockDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("denis")).thenReturn(mockDetails);
        when(jwtService.generateToken(mockDetails)).thenReturn("mocked_jwt_token");

        AuthResponse response = userService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("mocked_jwt_token");
        assertThat(response.getUsername()).isEqualTo("denis");
        assertThat(response.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    void register_shouldThrow_whenUsernameTaken() {
        when(userRepository.existsByUsername("denis")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");
    }

    @Test
    void register_shouldThrow_whenEmailTaken() {
        when(userRepository.existsByUsername("denis")).thenReturn(false);
        when(userRepository.existsByEmail("denis@email.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void login_shouldReturnToken_whenCredentialsValid() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("denis");
        loginRequest.setPassword("secret123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // authentication passes

        UserDetails mockDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("denis")).thenReturn(mockDetails);
        when(jwtService.generateToken(mockDetails)).thenReturn("login_jwt_token");
        when(userRepository.findByUsername("denis")).thenReturn(Optional.of(savedUser));

        AuthResponse response = userService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("login_jwt_token");
        assertThat(response.getUsername()).isEqualTo("denis");
    }

    @Test
    void getProfile_shouldReturnUser_whenExists() {
        when(userRepository.findByUsername("denis")).thenReturn(Optional.of(savedUser));

        User result = userService.getProfile("denis");

        assertThat(result.getUsername()).isEqualTo("denis");
        assertThat(result.getRole()).isEqualTo(User.Role.CUSTOMER);
    }

    @Test
    void getProfile_shouldThrow_whenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }
}
