package com.ironlibrary.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironlibrary.user_service.model.MembershipType;
import com.ironlibrary.user_service.model.User;
import com.ironlibrary.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para UserController usando MockMvc
 * Compatible con Spring Boot 3.4+ (sin @MockBean deprecated)
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class UserControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService; // Este será el mock

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public UserService userService() {
            return mock(UserService.class);
        }
    }

    @BeforeEach
    void setUp() {
        // Reset mock before each test
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
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void healthCheck_ShouldReturnHealthMessage() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Service is running on port 8082"));
    }
}
