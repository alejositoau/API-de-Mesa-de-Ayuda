package com.sena.security.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("El email ya está registrado: " + email);
    }
}
