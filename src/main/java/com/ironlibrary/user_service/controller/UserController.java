package com.ironlibrary.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones de usuarios
 * Endpoints base: /api/users
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users - Obtener todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Solicitud GET para obtener todos los usuarios");
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id} - Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Solicitud GET para obtener usuario con ID: {}", id);
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * GET /api/users/email/{email} - Obtener usuario por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        log.info("Solicitud GET para obtener usuario con email: {}", email);
        User user = userService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * GET /api/users/active - Obtener usuarios activos
     */
    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        log.info("Solicitud GET para obtener usuarios activos");
        List<User> users = userService.findActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/inactive - Obtener usuarios inactivos
     */
    @GetMapping("/inactive")
    public ResponseEntity<List<User>> getInactiveUsers() {
        log.info("Solicitud GET para obtener usuarios inactivos");
        List<User> users = userService.findInactiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/can-borrow - Obtener usuarios que pueden pedir prestado
     */
    @GetMapping("/can-borrow")
    public ResponseEntity<List<User>> getUsersWhoCanBorrow() {
        log.info("Solicitud GET para obtener usuarios que pueden pedir prestado");
        List<User> users = userService.findUsersWhoCanBorrow();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/membership?type=PREMIUM - Obtener usuarios por membresía
     */
    @GetMapping("/membership")
    public ResponseEntity<List<User>> getUsersByMembership(@RequestParam MembershipType type) {
        log.info("Solicitud GET para obtener usuarios de membresía: {}", type);
        List<User> users = userService.findByMembershipType(type);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/search/name?name=Juan - Buscar por nombre
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<User>> getUsersByName(@RequestParam String name) {
        log.info("Solicitud GET para buscar usuarios por nombre: {}", name);
        List<User> users = userService.findByName(name);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id}/validate - Validar si usuario puede pedir prestado
     */
    @GetMapping("/{id}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable Long id) {
        log.info("Solicitud GET para validar usuario ID: {}", id);
        boolean canBorrow = userService.validateUser(id);
        return ResponseEntity.ok(canBorrow);
    }

    /**
     * GET /api/users/stats - Obtener estadísticas de usuarios
     */
    @GetMapping("/stats")
    public ResponseEntity<UserService.UserStats> getUserStats() {
        log.info("Solicitud GET para obtener estadísticas de usuarios");
        UserService.UserStats stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * POST /api/users - Crear nuevo usuario
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Solicitud POST para crear nuevo usuario: {}", user.getName());
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    /**
     * PUT /api/users/{id} - Actualizar usuario completo
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("Solicitud PUT para actualizar usuario con ID: {}", id);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * PATCH /api/users/{id}/toggle-status - Activar/Desactivar usuario
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Long id) {
        log.info("Solicitud PATCH para cambiar estado del usuario ID: {}", id);
        User updatedUser = userService.toggleUserStatus(id);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * PATCH /api/users/{id}/membership?type=PREMIUM - Cambiar tipo de membresía
     */
    @PatchMapping("/{id}/membership")
    public ResponseEntity<User> updateMembershipType(@PathVariable Long id, @RequestParam MembershipType type) {
        log.info("Solicitud PATCH para actualizar membresía del usuario ID: {} a {}", id, type);
        User updatedUser = userService.updateMembershipType(id, type);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * DELETE /api/users/{id} - Eliminar usuario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Solicitud DELETE para eliminar usuario con ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint de health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("User Service is running on port 8082");
    }
}
