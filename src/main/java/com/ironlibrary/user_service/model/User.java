package com.ironlibrary.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Modelo User - Representa un usuario en el sistema
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false, length = 50)
    private MembershipType membershipType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    /**
     * Verifica si el usuario puede pedir libros prestados
     * @return true si el usuario está activo y tiene membresía válida
     */
    public boolean canBorrowBooks() {
        return isActive != null && isActive && membershipType != null;
    }

    /**
     * Obtiene el límite de libros que puede tener prestados según su membresía
     * @return número máximo de libros
     */
    public int getMaxBooksAllowed() {
        if (membershipType == null) return 0;

        return switch (membershipType) {
            case BASIC -> 3;
            case PREMIUM -> 10;
            case STUDENT -> 5;
        };
    }

    /**
     * Constructor para crear un usuario básico
     */
    public User(String name, String email, MembershipType membershipType) {
        this.name = name;
        this.email = email;
        this.membershipType = membershipType;
        this.isActive = true;
        this.registrationDate = LocalDate.now();
    }
}