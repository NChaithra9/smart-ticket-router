package com.example.smart_ticket_router.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.smart_ticket_router.entity.Ticket;
import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.enums.Priority;
import com.example.smart_ticket_router.enums.TicketStatus;

/**
 * Repository interface for performing CRUD operations
 * and custom queries on {@link Ticket} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard
 * database operations and defines additional query
 * methods for retrieving tickets based on user,
 * priority, and creation date.
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
     * Retrieves every ticket ordered by business priority — all
     * {@code HIGH} tickets first, then {@code MEDIUM}, then
     * {@code LOW} — and, within the same priority, by creation date
     * from newest to oldest.
     *
     * <p>
     * This is the default ordering used for the admin "all tickets"
     * dashboard when no priority filter is applied, so the most
     * urgent tickets always surface to the top regardless of when
     * they were submitted.
     * </p>
     *
     * @return all tickets sorted by priority severity, then recency
     */
    @Query("SELECT t FROM Ticket t ORDER BY "
            + "CASE t.priority "
            + "WHEN com.example.smart_ticket_router.enums.Priority.HIGH THEN 0 "
            + "WHEN com.example.smart_ticket_router.enums.Priority.MEDIUM THEN 1 "
            + "ELSE 2 END, t.createdAt DESC")
    List<Ticket> findAllOrderByPriorityRank();

    /**
     * Retrieves all tickets submitted by the specified user,
     * ordered by creation date in descending order.
     *
     * @param user the user whose tickets are to be retrieved
     * @return a list of the user's tickets sorted from newest to oldest
     */
    List<Ticket> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Retrieves all tickets with the specified priority.
     *
     * @param priority the priority level to filter by
     * @return a list of tickets matching the given priority
     */
    List<Ticket> findByPriority(Priority priority);

    /**
     * Retrieves all tickets with the specified priority,
     * ordered by creation date in descending order.
     *
     * @param priority the priority level to filter by
     * @return a list of tickets matching the given priority,
     *         sorted from newest to oldest
     */
    List<Ticket> findByPriorityOrderByCreatedAtDesc(Priority priority);

    /**
     * Retrieves all tickets with the specified status, ordered by
     * business priority (highest first) and then by creation date.
     *
     * @param status the workflow status to filter by
     * @return matching tickets sorted by priority severity, then recency
     */
    @Query("SELECT t FROM Ticket t WHERE t.status = :status ORDER BY "
            + "CASE t.priority "
            + "WHEN com.example.smart_ticket_router.enums.Priority.HIGH THEN 0 "
            + "WHEN com.example.smart_ticket_router.enums.Priority.MEDIUM THEN 1 "
            + "ELSE 2 END, t.createdAt DESC")
    List<Ticket> findByStatusOrderByPriorityRank(@Param("status") TicketStatus status);

    /**
     * Retrieves all tickets matching both the specified priority and
     * status, ordered by creation date in descending order.
     *
     * @param priority the priority level to filter by
     * @param status the workflow status to filter by
     * @return matching tickets sorted from newest to oldest
     */
    List<Ticket> findByPriorityAndStatusOrderByCreatedAtDesc(
            Priority priority, TicketStatus status);
}