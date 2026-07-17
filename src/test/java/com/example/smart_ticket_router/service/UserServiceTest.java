package com.example.smart_ticket_router.service;

import com.example.smart_ticket_router.entity.Role;
import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.exception.UserAlreadyExistsException;
import com.example.smart_ticket_router.repository.RoleRepository;
import com.example.smart_ticket_router.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserService}.
 *
 * <p>
 * These are plain Mockito-based unit tests (no Spring context is
 * started), so they run fast and do not require a database or any
 * other external dependency.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role("ROLE_USER");
    }

    @Test
    void registerUser_savesEncodedPasswordAndDefaultRole() {

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("ENCODED");
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User result = userService.registerUser("Jane", "jane@example.com", "secret123");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("jane@example.com", result.getEmail());
        assertEquals("ENCODED", result.getPassword());
        assertTrue(result.getRoles().contains(userRole));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_duplicateEmail_throwsUserAlreadyExistsException() {

        when(userRepository.findByEmail("jane@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.registerUser("Jane", "jane@example.com", "secret123"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_missingDefaultRole_throwsRuntimeException() {

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("ENCODED");
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                userService.registerUser("Jane", "jane@example.com", "secret123"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByEmail_returnsUserWhenPresent() {

        User existing = new User();
        existing.setEmail("jane@example.com");

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(existing));

        User result = userService.findByEmail("jane@example.com");

        assertNotNull(result);
        assertEquals("jane@example.com", result.getEmail());
    }

    @Test
    void findByEmail_returnsNullWhenAbsent() {

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertNull(userService.findByEmail("missing@example.com"));
    }
}
