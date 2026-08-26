package com.sena.security.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(Long id) {
        super("Ticket no encontrado con id: " + id);
    }
}
