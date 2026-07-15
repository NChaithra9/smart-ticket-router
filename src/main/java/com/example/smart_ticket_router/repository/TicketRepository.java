package com.example.smart_ticket_router.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.entity.User;

/**
 * Repository interface for performing CRUD operations
 * and custom queries on {@link Ticket} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard
 * database operations and defines additional query
 * methods for retrieving tickets based on user and
 * creation date.
 * </p>
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Retrieves all tickets submitted by the specified user.
     *
     * @param user the user whose tickets are to be retrieved
     * @return a list of tickets belonging to the user
     */
    List<Ticket> findByUser(User user);

    /**
     * Retrieves all tickets ordered by creation date
     * in descending order.
     *
     * @return a list of all tickets sorted from newest to oldest
     */
    List<Ticket> findAllByOrderByCreatedAtDesc();

    /**
     * Retrieves all tickets submitted by the specified user,
     * ordered by creation date in descending order.
     *
     * @param user the user whose tickets are to be retrieved
     * @return a list of the user's tickets sorted from newest to oldest
     */
    List<Ticket> findByUserOrderByCreatedAtDesc(User user);
}