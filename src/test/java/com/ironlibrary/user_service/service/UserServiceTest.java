package com.ironlibrary.user_service.service;

import com.ironlibrary.user_service.exception.EmailAlreadyExistsException;
import com.ironlibrary.user_service.exception.UserNotFoundException;
import com.ironlibrary.user_service.model.MembershipType;
import com.ironlibrary.user_service.model.User;
import com.ironlibrary.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para UserService
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Juan Pérez");
        testUser.setEmail("juan.perez@email.com");
        testUser.setMembershipType(MembershipType.PREMIUM);
        testUser.setIsActive(true);
        testUser.setRegistrationDate(LocalDate.now());
        testUser.setPhone("123456789");
        testUser.setAddress("Calle Principal 123");
    }

    @Test
    void findAllUsers_ShouldReturnAllUsers() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.findAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getName(), result.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    void findUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void findUserById_ShouldThrowException_WhenUserNotExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserById(1L)
        );

        assertEquals("Usuario no encontrado con ID: 1", exception.getMessage());
        verify(userRepository).findById(1L);
    }

    @Test
    void findUserByEmail_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByEmail("juan.perez@email.com")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findUserByEmail("juan.perez@email.com");

        // Then
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("juan.perez@email.com");
    }

    @Test
    void findUserByEmail_ShouldThrowException_WhenUserNotExists() {
        // Given
        when(userRepository.findByEmail("no.existe@email.com")).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserByEmail("no.existe@email.com")
        );

        assertTrue(exception.getMessage().contains("no.existe@email.com"));
        verify(userRepository).findByEmail("no.existe@email.com");
    }

    @Test
    void saveUser_ShouldReturnSavedUser_WhenValidUser() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.saveUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    void saveUser_ShouldThrowException_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // When & Then
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.saveUser(testUser)
        );

        assertTrue(exception.getMessage().contains("Ya existe un usuario con el email"));
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_ShouldSetDefaultValues_WhenNullValues() {
        // Given
        testUser.setIsActive(null);
        testUser.setRegistrationDate(null);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.saveUser(testUser);

        // Then
        assertTrue(testUser.getIsActive());
        assertEquals(LocalDate.now(), testUser.getRegistrationDate());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenValidData() {
        // Given
        User updatedData = new User();
        updatedData.setName("Juan Carlos Pérez");
        updatedData.setEmail("juan.carlos@email.com");
        updatedData.setMembershipType(MembershipType.STUDENT);
        updatedData.setIsActive(false);
        updatedData.setPhone("987654321");
        updatedData.setAddress("Nueva Dirección 456");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(updatedData.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUser(1L, updatedData);

        // Then
        assertEquals(updatedData.getName(), testUser.getName());
        assertEquals(updatedData.getEmail(), testUser.getEmail());
        assertEquals(updatedData.getMembershipType(), testUser.getMembershipType());
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    void toggleUserStatus_ShouldToggleStatus() {
        // Given
        testUser.setIsActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.toggleUserStatus(1L);

        // Then
        assertFalse(testUser.getIsActive());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateMembershipType_ShouldUpdateMembership() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateMembershipType(1L, MembershipType.BASIC);

        // Then
        assertEquals(MembershipType.BASIC, testUser.getMembershipType());
        verify(userRepository).save(testUser);
    }

    @Test
    void findByMembershipType_ShouldReturnUsersOfType() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByMembershipType(MembershipType.PREMIUM)).thenReturn(users);

        // When
        List<User> result = userService.findByMembershipType(MembershipType.PREMIUM);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByMembershipType(MembershipType.PREMIUM);
    }

    @Test
    void findActiveUsers_ShouldReturnActiveUsers() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByIsActiveTrue()).thenReturn(users);

        // When
        List<User> result = userService.findActiveUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByIsActiveTrue();
    }

    @Test
    void validateUser_ShouldReturnTrue_WhenUserCanBorrow() {
        // Given
        testUser.setIsActive(true);
        testUser.setMembershipType(MembershipType.PREMIUM);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.validateUser(1L);

        // Then
        assertTrue(result);
        verify(userRepository).findById(1L);
    }

    @Test
    void validateUser_ShouldReturnFalse_WhenUserIsInactive() {
        // Given
        testUser.setIsActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.validateUser(1L);

        // Then
        assertFalse(result);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserStats_ShouldReturnCorrectStats() {
        // Given
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countActiveUsers()).thenReturn(85L);
        when(userRepository.countByMembershipType(MembershipType.BASIC)).thenReturn(30L);
        when(userRepository.countByMembershipType(MembershipType.PREMIUM)).thenReturn(40L);
        when(userRepository.countByMembershipType(MembershipType.STUDENT)).thenReturn(30L);

        // When
        UserService.UserStats result = userService.getUserStats();

        // Then
        assertEquals(100L, result.totalUsers);
        assertEquals(85L, result.activeUsers);
        assertEquals(30L, result.basicUsers);
        assertEquals(40L, result.premiumUsers);
        assertEquals(30L, result.studentUsers);
    }
}
