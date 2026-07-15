package com.example.smart_ticket_router.enums;

/**
 * Represents the categories used to classify support tickets.
 *
 * <p>Each submitted ticket is analyzed by the AI model and
 * assigned to one of these categories based on its content.
 */
public enum TicketCategory {

    /**
     * Issues related to billing, payments, invoices,
     * subscriptions, or refunds.
     */
    BILLING,

    /**
     * Issues related to user authentication, including
     * login failures, password resets, and multi-factor authentication.
     */
    AUTHENTICATION,

    /**
     * Technical issues such as software bugs, application errors,
     * performance problems, or system failures.
     */
    TECHNICAL,

    /**
     * Account-related issues including profile management,
     * account settings, and account access.
     */
    ACCOUNT,

    /**
     * Requests for new features or enhancements
     * to the application.
     */
    FEATURE_REQUEST,

    /**
     * General customer support inquiries that do not
     * fit into any other category.
     */
    GENERAL_SUPPORT
}