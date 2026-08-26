package com.sena.security.controller;

import com.sena.security.dto.TicketEstadoRequest;
import com.sena.security.dto.TicketRequest;
import com.sena.security.dto.TicketResponse;
import com.sena.security.model.Ticket;
import com.sena.security.model.User;
import com.sena.security.repository.UserRepository;
import com.sena.security.service.TicketsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketsService ticketsService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<TicketResponse> crearTicket(@Valid @RequestBody TicketRequest request,
                                                        Authentication authentication) {
        User usuario = usuarioAutenticado(authentication);
        Ticket nuevoTicket = ticketsService.crearTicket(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketResponse.from(nuevoTicket));
    }

    @GetMapping("/mios")
    public ResponseEntity<List<TicketResponse>> misTickets(Authentication authentication) {
        User usuario = usuarioAutenticado(authentication);
        List<TicketResponse> tickets = ticketsService.listarMios(usuario).stream()
                .map(TicketResponse::from)
                .toList();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/vencidos")
    public ResponseEntity<List<TicketResponse>> listarVencidos() {
        List<TicketResponse> tickets = ticketsService.listarVencidos().stream()
                .map(TicketResponse::from)
                .toList();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> listarTodos() {
        List<TicketResponse> tickets = ticketsService.listarTodos().stream()
                .map(TicketResponse::from)
                .toList();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> obtenerTicket(@PathVariable Long id, Authentication authentication) {
        User usuario = usuarioAutenticado(authentication);
        Ticket ticket = ticketsService.obtenerPorId(id, usuario);
        return ResponseEntity.ok(TicketResponse.from(ticket));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<TicketResponse> cambiarEstado(@PathVariable Long id,
                                                          @Valid @RequestBody TicketEstadoRequest request) {
        Ticket ticket = ticketsService.cambiarEstado(id, request);
        return ResponseEntity.ok(TicketResponse.from(ticket));
    }

    private User usuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}
