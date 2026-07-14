package com.example.smart_ticket_router.model;

import com.example.smart_ticket_router.enums.AssignedTeam;
import com.example.smart_ticket_router.enums.Priority;
import com.example.smart_ticket_router.enums.TicketCategory;

public class TicketResponse {

    private TicketCategory category;

    private Priority priority;

    private AssignedTeam assignedTeam;

    private String reason;

    public TicketResponse() {
    }

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public AssignedTeam getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(AssignedTeam assignedTeam) {
        this.assignedTeam = assignedTeam;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}