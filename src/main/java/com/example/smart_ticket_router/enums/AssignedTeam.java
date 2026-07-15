package com.example.smart_ticket_router.enums;

/**
 * Represents the support teams responsible for resolving
 * routed support tickets.
 *
 * <p>After analyzing a ticket, the AI assigns it to one of
 * these teams based on the nature of the issue.
 */
public enum AssignedTeam {

    /**
     * Handles billing, payments, invoices, and financial issues.
     */
    FINANCE_SUPPORT,

    /**
     * Handles account-related issues such as login,
     * registration, password reset, and profile management.
     */
    ACCOUNT_SUPPORT,

    /**
     * Handles technical problems, software bugs,
     * system errors, and application issues.
     */
    TECHNICAL_SUPPORT,

    /**
     * Handles product-related requests including
     * feature requests and product defects.
     */
    PRODUCT_TEAM,

    /**
     * Handles general customer inquiries and
     * issues that do not belong to other teams.
     */
    CUSTOMER_SUPPORT
}