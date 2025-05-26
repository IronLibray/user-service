package com.ironlibrary.user_service.exception;

/**
 * Excepción lanzada cuando ya existe un usuario con el email proporcionado
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
