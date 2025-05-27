package com.ironlibrary.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironlibrary.user_service.model.MembershipType;
import com.ironlibrary.user_service.model.User;
import com.ironlibrary.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para UserController usando MockMvc
 * Enfoque limpio con @TestConfiguration para Spring Boot 3.4+
 */
@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerMockMvcTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public UserService userService() {
            return mock(UserService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Resetear el mock antes de cada test
        reset(userService);

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
    void getAllUsers_ShouldReturnUserListAsJson() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Juan Pérez"))
                .andExpect(jsonPath("$[0].email").value("juan.perez@email.com"))
                .andExpect(jsonPath("$[0].membershipType").value("PREMIUM"))
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(userService).findAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUserAsJson() throws Exception {
        // Given
        when(userService.findUserById(1L)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan.perez@email.com"))
                .andExpect(jsonPath("$.membershipType").value("PREMIUM"));

        verify(userService).findUserById(1L);
    }

    @Test
    void getUserByEmail_ShouldReturnUserAsJson() throws Exception {
        // Given
        when(userService.findUserByEmail("juan.perez@email.com")).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/email/juan.perez@email.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("juan.perez@email.com"));

        verify(userService).findUserByEmail("juan.perez@email.com");
    }

    @Test
    void getActiveUsers_ShouldReturnActiveUsersOnly() throws Exception {
        // Given
        List<User> activeUsers = Arrays.asList(testUser);
        when(userService.findActiveUsers()).thenReturn(activeUsers);

        // When & Then
        mockMvc.perform(get("/api/users/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(userService).findActiveUsers();
    }

    @Test
    void getInactiveUsers_ShouldReturnInactiveUsers() throws Exception {
        // Given
        testUser.setIsActive(false);
        List<User> inactiveUsers = Arrays.asList(testUser);
        when(userService.findInactiveUsers()).thenReturn(inactiveUsers);

        // When & Then
        mockMvc.perform(get("/api/users/inactive"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].isActive").value(false));

        verify(userService).findInactiveUsers();
    }

    @Test
    void getUsersWhoCanBorrow_ShouldReturnEligibleUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findUsersWhoCanBorrow()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users/can-borrow"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(userService).findUsersWhoCanBorrow();
    }

    @Test
    void getUsersByMembership_ShouldReturnUsersOfMembership() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findByMembershipType(MembershipType.PREMIUM)).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users/membership")
                        .param("type", "PREMIUM"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].membershipType").value("PREMIUM"));

        verify(userService).findByMembershipType(MembershipType.PREMIUM);
    }

    @Test
    void getUsersByName_ShouldReturnUsersByName() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findByName("Juan")).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users/search/name")
                        .param("name", "Juan"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Juan Pérez"));

        verify(userService).findByName("Juan");
    }

    @Test
    void validateUser_ShouldReturnBooleanResult() throws Exception {
        // Given
        when(userService.validateUser(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/users/1/validate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        verify(userService).validateUser(1L);
    }

    @Test
    void getUserStats_ShouldReturnStatisticsAsJson() throws Exception {
        // Given
        UserService.UserStats stats = new UserService.UserStats(100L, 85L, 30L, 40L, 30L);
        when(userService.getUserStats()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/users/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalUsers").value(100))
                .andExpect(jsonPath("$.activeUsers").value(85))
                .andExpect(jsonPath("$.basicUsers").value(30))
                .andExpect(jsonPath("$.premiumUsers").value(40))
                .andExpect(jsonPath("$.studentUsers").value(30));

        verify(userService).getUserStats();
    }

    @Test
    void createUser_ShouldReturnCreatedUserWithStatus201() throws Exception {
        // Given
        User newUser = new User();
        newUser.setName("Ana García");
        newUser.setEmail("ana.garcia@email.com");
        newUser.setMembershipType(MembershipType.BASIC);
        newUser.setIsActive(true);
        newUser.setPhone("987654321");
        newUser.setAddress("Calle Nueva 456");

        when(userService.saveUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Juan Pérez"));

        verify(userService).saveUser(any(User.class));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Given
        User updateData = new User();
        updateData.setName("Juan Carlos Pérez");
        updateData.setEmail("juan.carlos@email.com");
        updateData.setMembershipType(MembershipType.STUDENT);
        updateData.setIsActive(false);

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Juan Carlos Pérez");
        updatedUser.setEmail("juan.carlos@email.com");
        updatedUser.setMembershipType(MembershipType.STUDENT);
        updatedUser.setIsActive(false);

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Juan Carlos Pérez"))
                .andExpect(jsonPath("$.membershipType").value("STUDENT"))
                .andExpect(jsonPath("$.isActive").value(false));

        verify(userService).updateUser(eq(1L), any(User.class));
    }

    @Test
    void toggleUserStatus_ShouldReturnToggledUser() throws Exception {
        // Given
        User toggledUser = new User();
        toggledUser.setId(1L);
        toggledUser.setName("Juan Pérez");
        toggledUser.setEmail("juan.perez@email.com");
        toggledUser.setMembershipType(MembershipType.PREMIUM);
        toggledUser.setIsActive(false); // Cambiado a false

        when(userService.toggleUserStatus(1L)).thenReturn(toggledUser);

        // When & Then
        mockMvc.perform(patch("/api/users/1/toggle-status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isActive").value(false));

        verify(userService).toggleUserStatus(1L);
    }

    @Test
    void updateMembershipType_ShouldReturnUpdatedUser() throws Exception {
        // Given
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Juan Pérez");
        updatedUser.setEmail("juan.perez@email.com");
        updatedUser.setMembershipType(MembershipType.BASIC);
        updatedUser.setIsActive(true);

        when(userService.updateMembershipType(1L, MembershipType.BASIC)).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(patch("/api/users/1/membership")
                        .param("type", "BASIC"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.membershipType").value("BASIC"));

        verify(userService).updateMembershipType(1L, MembershipType.BASIC);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void healthCheck_ShouldReturnHealthMessage() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Service is running on port 8082"));
    }
}
