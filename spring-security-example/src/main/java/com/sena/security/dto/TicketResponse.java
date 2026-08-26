package com.sena.security.dto;

import com.sena.security.model.Ticket;
import com.sena.security.model.enums.Estado;
import com.sena.security.model.enums.Prioridad;

import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String titulo,
        String descripcion,
        Prioridad prioridad,
        Estado estado,
        LocalDateTime creadoEn,
        LocalDateTime slaVenceEn,
        String creadoPorEmail,
        boolean vencido
) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getDescripcion(),
                ticket.getPrioridad(),
                ticket.getEstado(),
                ticket.getCreadoEn(),
                ticket.getSlaVenceEn(),
                ticket.getCreadoPor().getEmail(),
                ticket.isVencido()
        );
    }
}
