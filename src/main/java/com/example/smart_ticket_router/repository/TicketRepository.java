package com.example.smart_ticket_router.repository;

import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUser(User user);
    List<Ticket> findAllByOrderByCreatedAtDesc();

List<Ticket> findByUserOrderByCreatedAtDesc(User user);

}