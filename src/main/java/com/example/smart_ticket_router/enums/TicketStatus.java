package com.example.smart_ticket_router.enums;

/**
 * Represents the workflow status of a support ticket.
 *
 * <p>
 * Unlike {@link Priority}, {@link TicketCategory} and
 * {@link AssignedTeam} — which are all set once by the AI when the
 * ticket is routed — the status is expected to change over time as an
 * administrator works the ticket. New tickets always start as
 * {@link #OPEN}.
 */
public enum TicketStatus {

    /**
     * The ticket has just been submitted and has not yet been picked
     * up by anyone. This is the default status for every new ticket.
     */
    OPEN,

    /**
     * An administrator is actively working on the ticket
     * ("running").
     */
    IN_PROGRESS,

    /**
     * The underlying issue has been fixed and the ticket has been
     * resolved ("solved").
     */
    RESOLVED,

    /**
     * The ticket is fully closed and archived. Used once a resolved
     * ticket has also been confirmed/verified and no further action
     * is expected.
     */
    CLOSED
}
