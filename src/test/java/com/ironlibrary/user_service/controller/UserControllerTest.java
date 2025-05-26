package com.ironlibrary.user_service.controller;


import com.ironlibrary.user_service.model.MembershipType;
import com.ironlibrary.user_service.model.User;
import com.ironlibrary.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios simples para UserController
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

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
    void getAllUsers_ShouldReturnUserList() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findAllUsers()).thenReturn(users);

        // When
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Juan Pérez", response.getBody().get(0).getName());
        verify(userService).findAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        // Given
        when(userService.findUserById(1L)).thenReturn(testUser);

        // When
        ResponseEntity<User> response = userController.getUserById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan Pérez", response.getBody().getName());
        assertEquals("juan.perez@email.com", response.getBody().getEmail());
        verify(userService).findUserById(1L);
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        // Given
        when(userService.findUserByEmail("juan.perez@email.com")).thenReturn(testUser);

        // When
        ResponseEntity<User> response = userController.getUserByEmail("juan.perez@email.com");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("juan.perez@email.com", response.getBody().getEmail());
        verify(userService).findUserByEmail("juan.perez@email.com");
    }

    @Test
    void getActiveUsers_ShouldReturnActiveUsers() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findActiveUsers()).thenReturn(users);

        // When
        ResponseEntity<List<User>> response = userController.getActiveUsers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).getIsActive());
        verify(userService).findActiveUsers();
    }

    @Test
    void getInactiveUsers_ShouldReturnInactiveUsers() {
        // Given
        testUser.setIsActive(false);
        List<User> users = Arrays.asList(testUser);
        when(userService.findInactiveUsers()).thenReturn(users);

        // When
        ResponseEntity<List<User>> response = userController.getInactiveUsers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertFalse(response.getBody().get(0).getIsActive());
        verify(userService).findInactiveUsers();
    }

    @Test
    void getUsersWhoCanBorrow_ShouldReturnEligibleUsers() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findUsersWhoCanBorrow()).thenReturn(users);

        // When
        ResponseEntity<List<User>> response = userController.getUsersWhoCanBorrow();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userService).findUsersWhoCanBorrow();
    }

    @Test
    void getUsersByMembership_ShouldReturnUsersOfMembership() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findByMembershipType(MembershipType.PREMIUM)).thenReturn(users);

        // When
        ResponseEntity<List<User>> response = userController.getUsersByMembership(MembershipType.PREMIUM);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(MembershipType.PREMIUM, response.getBody().get(0).getMembershipType());
        verify(userService).findByMembershipType(MembershipType.PREMIUM);
    }

    @Test
    void getUsersByName_ShouldReturnUsersByName() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findByName("Juan")).thenReturn(users);

        // When
        ResponseEntity<List<User>> response = userController.getUsersByName("Juan");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).getName().contains("Juan"));
        verify(userService).findByName("Juan");
    }

    @Test
    void validateUser_ShouldReturnTrue_WhenUserCanBorrow() {
        // Given
        when(userService.validateUser(1L)).thenReturn(true);

        // When
        ResponseEntity<Boolean> response = userController.validateUser(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(userService).validateUser(1L);
    }

    @Test
    void validateUser_ShouldReturnFalse_WhenUserCannotBorrow() {
        // Given
        when(userService.validateUser(1L)).thenReturn(false);

        // When
        ResponseEntity<Boolean> response = userController.validateUser(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
        verify(userService).validateUser(1L);
    }

    @Test
    void getUserStats_ShouldReturnStats() {
        // Given
        UserService.UserStats stats = new UserService.UserStats(100L, 85L, 30L, 40L, 30L);
        when(userService.getUserStats()).thenReturn(stats);

        // When
        ResponseEntity<UserService.UserStats> response = userController.getUserStats();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().totalUsers);
        assertEquals(85L, response.getBody().activeUsers);
        verify(userService).getUserStats();
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        // Given
        when(userService.saveUser(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<User> response = userController.createUser(testUser);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan Pérez", response.getBody().getName());
        verify(userService).saveUser(any(User.class));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        // Given
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Juan Carlos Pérez");
        updatedUser.setEmail("juan.carlos@email.com");
        updatedUser.setMembershipType(MembershipType.STUDENT);
        updatedUser.setIsActive(false);

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // When
        ResponseEntity<User> response = userController.updateUser(1L, updatedUser);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan Carlos Pérez", response.getBody().getName());
        assertEquals("juan.carlos@email.com", response.getBody().getEmail());
        verify(userService).updateUser(eq(1L), any(User.class));
    }

    @Test
    void toggleUserStatus_ShouldReturnToggledUser() {
        // Given
        testUser.setIsActive(false);
        when(userService.toggleUserStatus(1L)).thenReturn(testUser);

        // When
        ResponseEntity<User> response = userController.toggleUserStatus(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getIsActive());
        verify(userService).toggleUserStatus(1L);
    }

    @Test
    void updateMembershipType_ShouldReturnUpdatedUser() {
        // Given
        testUser.setMembershipType(MembershipType.BASIC);
        when(userService.updateMembershipType(1L, MembershipType.BASIC)).thenReturn(testUser);

        // When
        ResponseEntity<User> response = userController.updateMembershipType(1L, MembershipType.BASIC);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(MembershipType.BASIC, response.getBody().getMembershipType());
        verify(userService).updateMembershipType(1L, MembershipType.BASIC);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When
        ResponseEntity<Void> response = userController.deleteUser(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(1L);
    }

    @Test
    void healthCheck_ShouldReturnOk() {
        // When
        ResponseEntity<String> response = userController.healthCheck();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Service is running on port 8082", response.getBody());
    }
}