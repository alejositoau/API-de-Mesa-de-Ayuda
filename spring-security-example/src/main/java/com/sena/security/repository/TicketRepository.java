package com.sena.security.repository;

import com.sena.security.model.Ticket;
import com.sena.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCreadoPor(User creadoPor);
}
