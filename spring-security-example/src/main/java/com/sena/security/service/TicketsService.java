package com.sena.security.service;

import com.sena.security.dto.TicketEstadoRequest;
import com.sena.security.dto.TicketRequest;
import com.sena.security.exception.TicketNotFoundException;
import com.sena.security.model.Role;
import com.sena.security.model.Ticket;
import com.sena.security.model.User;
import com.sena.security.model.enums.Estado;
import com.sena.security.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketsService {

    private final TicketRepository ticketRepository;

    public Ticket crearTicket(TicketRequest request, User creadoPor) {
        LocalDateTime ahora = LocalDateTime.now();

        int horasSla = switch (request.prioridad()) {
            case ALTA -> 4;
            case MEDIA -> 24;
            case BAJA -> 72;
        };

        Ticket ticket = Ticket.builder()
                .titulo(request.titulo())
                .descripcion(request.descripcion())
                .prioridad(request.prioridad())
                .estado(Estado.ABIERTO)
                .creadoEn(ahora)
                .slaVenceEn(ahora.plusHours(horasSla))
                .creadoPor(creadoPor)
                .build();

        return ticketRepository.save(ticket);
    }

    public List<Ticket> listarMios(User usuario) {
        return ticketRepository.findByCreadoPor(usuario);
    }

    public List<Ticket> listarTodos() {
        return ticketRepository.findAll();
    }

    public List<Ticket> listarVencidos() {
        return ticketRepository.findAll().stream()
                .filter(Ticket::isVencido)
                .toList();
    }

    public Ticket obtenerPorId(Long id, User solicitante) {
        Ticket ticket = buscarOFallar(id);

        boolean esDueno = ticket.getCreadoPor().getId().equals(solicitante.getId());
        boolean tienePermisoAmpliado = solicitante.getRole() == Role.SOPORTE || solicitante.getRole() == Role.ADMIN;

        if (!esDueno && !tienePermisoAmpliado) {
            throw new AccessDeniedException("No puede consultar tickets de otros usuarios");
        }

        return ticket;
    }

    public Ticket cambiarEstado(Long id, TicketEstadoRequest request) {
        Ticket ticket = buscarOFallar(id);
        ticket.setEstado(request.estado());
        return ticketRepository.save(ticket);
    }

    private Ticket buscarOFallar(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }
}
