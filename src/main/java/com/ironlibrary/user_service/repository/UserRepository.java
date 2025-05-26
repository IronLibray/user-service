package com.ironlibrary.user_service.repository;

import com.ironlibrary.user_service.model.MembershipType;
import com.ironlibrary.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Buscar usuario por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Buscar usuarios por tipo de membresía
     */
    List<User> findByMembershipType(MembershipType membershipType);

    /**
     * Buscar usuarios activos
     */
    List<User> findByIsActiveTrue();

    /**
     * Buscar usuarios inactivos
     */
    List<User> findByIsActiveFalse();

    /**
     * Buscar usuarios por nombre (búsqueda insensible a mayúsculas)
     */
    List<User> findByNameContainingIgnoreCase(String name);

    /**
     * Buscar usuarios registrados entre fechas
     */
    List<User> findByRegistrationDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Buscar usuarios por membresía y estado activo
     */
    List<User> findByMembershipTypeAndIsActive(MembershipType membershipType, Boolean isActive);

    /**
     * Contar usuarios por tipo de membresía
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.membershipType = :membershipType")
    Long countByMembershipType(@Param("membershipType") MembershipType membershipType);

    /**
     * Contar usuarios activos
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();

    /**
     * Verificar si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);

    /**
     * Buscar usuarios registrados hoy
     */
    @Query("SELECT u FROM User u WHERE u.registrationDate = CURRENT_DATE")
    List<User> findUsersRegisteredToday();

    /**
     * Buscar usuarios que pueden pedir libros prestados
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.membershipType IS NOT NULL")
    List<User> findUsersWhoCanBorrow();
}
