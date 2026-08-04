package com.sena.security.controller;

import com.sena.security.model.Ticket;
import com.sena.security.service.TicketsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tickets")

public class TicketController {
    
    @Autowired
    private TicketsService ticketsService;

    @PostMapping
    public ResponseEntity<Ticket> crearTicket(@RequestBody Ticket ticket) {
        Ticket nuevoTicket = ticketsService.crearTicket(ticket);
        return ResponseEntity.ok(nuevoTicket);
    }
    @GetMapping("/{id}/vencido")
    public ResponseEntity<Boolean> verificarVencimiento(@PathVariable Long id) {
        boolean vencido = ticketsService.isTicketVencido(id);
        return ResponseEntity.ok(vencido);
    }

}
