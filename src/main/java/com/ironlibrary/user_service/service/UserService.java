package com.ironlibrary.user_service.service;

import com.ironlibrary.user_service.exception.EmailAlreadyExistsException;
import com.ironlibrary.user_service.exception.UserNotFoundException;
import com.ironlibrary.user_service.model.MembershipType;
import com.ironlibrary.user_service.model.User;
import com.ironlibrary.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para la lógica de negocio de usuarios
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * Obtener todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        log.info("Obteniendo todos los usuarios");
        return userRepository.findAll();
    }

    /**
     * Buscar usuario por ID
     */
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Buscar usuario por email
     */
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        log.info("Buscando usuario con email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + email));
    }

    /**
     * Guardar nuevo usuario
     */
    public User saveUser(User user) {
        log.info("Guardando nuevo usuario: {}", user.getName());

        // Verificar si el email ya existe
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con el email: " + user.getEmail());
        }

        // Establecer valores por defecto
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        if (user.getRegistrationDate() == null) {
            user.setRegistrationDate(LocalDate.now());
        }

        User savedUser = userRepository.save(user);
        log.info("Usuario guardado exitosamente con ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Actualizar usuario existente
     */
    public User updateUser(Long id, User userUpdate) {
        log.info("Actualizando usuario con ID: {}", id);
        User existingUser = findUserById(id);

        // Verificar si el nuevo email ya existe en otro usuario
        if (!existingUser.getEmail().equals(userUpdate.getEmail()) &&
                userRepository.existsByEmail(userUpdate.getEmail())) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con el email: " + userUpdate.getEmail());
        }

        existingUser.setName(userUpdate.getName());
        existingUser.setEmail(userUpdate.getEmail());
        existingUser.setMembershipType(userUpdate.getMembershipType());
        existingUser.setIsActive(userUpdate.getIsActive());
        existingUser.setPhone(userUpdate.getPhone());
        existingUser.setAddress(userUpdate.getAddress());

        User updatedUser = userRepository.save(existingUser);
        log.info("Usuario actualizado exitosamente");
        return updatedUser;
    }

    /**
     * Eliminar usuario
     */
    public void deleteUser(Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        User user = findUserById(id);
        userRepository.delete(user);
        log.info("Usuario eliminado exitosamente");
    }

    /**
     * Activar/Desactivar usuario
     */
    public User toggleUserStatus(Long id) {
        log.info("Cambiando estado del usuario con ID: {}", id);
        User user = findUserById(id);
        user.setIsActive(!user.getIsActive());
        User updatedUser = userRepository.save(user);
        log.info("Usuario {} {}", updatedUser.getIsActive() ? "activado" : "desactivado", "exitosamente");
        return updatedUser;
    }

    /**
     * Cambiar tipo de membresía
     */
    public User updateMembershipType(Long id, MembershipType membershipType) {
        log.info("Actualizando membresía del usuario ID: {} a {}", id, membershipType);
        User user = findUserById(id);
        user.setMembershipType(membershipType);
        User updatedUser = userRepository.save(user);
        log.info("Membresía actualizada exitosamente");
        return updatedUser;
    }

    /**
     * Buscar usuarios por tipo de membresía
     */
    @Transactional(readOnly = true)
    public List<User> findByMembershipType(MembershipType membershipType) {
        log.info("Buscando usuarios por membresía: {}", membershipType);
        return userRepository.findByMembershipType(membershipType);
    }

    /**
     * Obtener usuarios activos
     */
    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        log.info("Obteniendo usuarios activos");
        return userRepository.findByIsActiveTrue();
    }

    /**
     * Obtener usuarios inactivos
     */
    @Transactional(readOnly = true)
    public List<User> findInactiveUsers() {
        log.info("Obteniendo usuarios inactivos");
        return userRepository.findByIsActiveFalse();
    }

    /**
     * Buscar usuarios por nombre
     */
    @Transactional(readOnly = true)
    public List<User> findByName(String name) {
        log.info("Buscando usuarios por nombre: {}", name);
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Validar si un usuario puede pedir libros prestados
     */
    @Transactional(readOnly = true)
    public boolean validateUser(Long id) {
        log.info("Validando usuario con ID: {}", id);
        User user = findUserById(id);
        boolean canBorrow = user.canBorrowBooks();
        log.info("Usuario {} {} pedir libros prestados",
                user.getName(), canBorrow ? "SÍ puede" : "NO puede");
        return canBorrow;
    }

    /**
     * Obtener usuarios que pueden pedir prestado
     */
    @Transactional(readOnly = true)
    public List<User> findUsersWhoCanBorrow() {
        log.info("Obteniendo usuarios que pueden pedir prestado");
        return userRepository.findUsersWhoCanBorrow();
    }

    /**
     * Obtener estadísticas de usuarios
     */
    @Transactional(readOnly = true)
    public UserStats getUserStats() {
        log.info("Obteniendo estadísticas de usuarios");

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countActiveUsers();
        long basicUsers = userRepository.countByMembershipType(MembershipType.BASIC);
        long premiumUsers = userRepository.countByMembershipType(MembershipType.PREMIUM);
        long studentUsers = userRepository.countByMembershipType(MembershipType.STUDENT);

        return new UserStats(totalUsers, activeUsers, basicUsers, premiumUsers, studentUsers);
    }

    /**
     * Clase interna para estadísticas
     */
    public static class UserStats {
        public final long totalUsers;
        public final long activeUsers;
        public final long basicUsers;
        public final long premiumUsers;
        public final long studentUsers;

        public UserStats(long totalUsers, long activeUsers, long basicUsers,
                         long premiumUsers, long studentUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.basicUsers = basicUsers;
            this.premiumUsers = premiumUsers;
            this.studentUsers = studentUsers;
        }
    }
}
