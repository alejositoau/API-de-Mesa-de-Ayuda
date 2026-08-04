package com.sena.security.service;

import com.sena.security.model.Ticket;
import com.sena.security.model.enums.Estado;
import com.sena.security.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TicketsService {
    
    @Autowired
    private TicketRepository ticketRepository;

    public Ticket crearTicket (Ticket ticket){

        ticket.setCreadoEn(LocalDateTime.now());

            ticket.setEstado(Estado.ABIERTO);

        int horasSla = switch (ticket.getPrioridad()){
            case ALTA -> 4;
            case MEDIA -> 24;
            case BAJA -> 72;
        };

        ticket.setSlaVenceEn(ticket.getCreadoEn().plusHours(horasSla));

        return ticketRepository.save(ticket);

    }

    // Regla de negocio: SI un ticket se vence y supera la fecha SLA y no está terminado se renueva

    public boolean isTicketVencido(Long ticketId){

        Ticket ticket =  ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        if(ticket.getEstado() == Estado.RESUELTO){
            return false;
        }
        return LocalDateTime.now().isAfter(ticket.getSlaVenceEn());
    }

}
