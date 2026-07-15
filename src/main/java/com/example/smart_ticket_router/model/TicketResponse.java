package com.example.smart_ticket_router.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.smart_ticket_router.enums.AssignedTeam;
import com.example.smart_ticket_router.enums.Priority;
import com.example.smart_ticket_router.enums.TicketCategory;

/**
 * Represents the response returned after a support ticket
 * has been analyzed and routed.
 * <p>
 * This class contains the predicted ticket category,
 * priority, assigned support team, and the reason
 * for the classification.
 * </p>
 */
public class TicketResponse {

    /**
     * Logger for TicketResponse.
     */
    private static final Logger logger = LoggerFactory.getLogger(TicketResponse.class);

    /**
     * Predicted category of the support ticket.
     */
    private TicketCategory category;

    /**
     * Predicted priority level of the support ticket.
     */
    private Priority priority;

    /**
     * Support team assigned to handle the ticket.
     */
    private AssignedTeam assignedTeam;

    /**
     * Explanation for the ticket classification.
     */
    private String reason;

    /**
     * Default constructor.
     */
    public TicketResponse() {
        logger.debug("TicketResponse object created.");
    }

    /**
     * Returns the predicted ticket category.
     *
     * @return the ticket category
     */
    public TicketCategory getCategory() {
        return category;
    }

    /**
     * Sets the predicted ticket category.
     *
     * @param category the ticket category
     */
    public void setCategory(TicketCategory category) {
        logger.debug("Setting ticket category.");
        this.category = category;
    }

    /**
     * Returns the predicted ticket priority.
     *
     * @return the ticket priority
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the predicted ticket priority.
     *
     * @param priority the ticket priority
     */
    public void setPriority(Priority priority) {
        logger.debug("Setting ticket priority.");
        this.priority = priority;
    }

    /**
     * Returns the assigned support team.
     *
     * @return the assigned support team
     */
    public AssignedTeam getAssignedTeam() {
        return assignedTeam;
    }

    /**
     * Sets the assigned support team.
     *
     * @param assignedTeam the support team assigned to the ticket
     */
    public void setAssignedTeam(AssignedTeam assignedTeam) {
        logger.debug("Setting assigned support team.");
        this.assignedTeam = assignedTeam;
    }

    /**
     * Returns the reason for the ticket classification.
     *
     * @return the classification reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason for the ticket classification.
     *
     * @param reason the explanation for the classification
     */
    public void setReason(String reason) {
        logger.debug("Setting ticket classification reason.");
        this.reason = reason;
    }
}