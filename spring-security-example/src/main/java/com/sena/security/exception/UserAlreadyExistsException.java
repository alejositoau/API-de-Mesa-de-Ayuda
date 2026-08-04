package com.sena.security.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String username) {
        super("El usuario ya existe: " + username);
    }
}
