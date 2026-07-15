package com.example.smart_ticket_router.prompt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class responsible for constructing the prompt
 * sent to the OpenAI model for ticket classification.
 * <p>
 * The generated prompt instructs the AI to analyze a
 * customer support ticket and return a structured JSON
 * containing the ticket category, priority, assigned team,
 * and the reason for the classification.
 * </p>
 */
public class PromptBuilder {

    /**
     * Logger for PromptBuilder.
     */
    private static final Logger logger = LoggerFactory.getLogger(PromptBuilder.class);

    /**
     * Builds the prompt for the given support ticket.
     *
     * @param ticket the customer support ticket text
     * @return the formatted prompt to send to the AI model
     */
    public static String buildPrompt(String ticket) {

        logger.info("Building AI prompt for ticket classification.");

        return """
You are an expert customer support ticket router.

Analyze the support ticket and return ONLY valid JSON.

Rules:

Categories:
- BILLING
- AUTHENTICATION
- TECHNICAL
- ACCOUNT
- FEATURE_REQUEST
- GENERAL_SUPPORT

Priorities:
- HIGH
- MEDIUM
- LOW

Teams:
- FINANCE_SUPPORT
- ACCOUNT_SUPPORT
- TECHNICAL_SUPPORT
- PRODUCT_TEAM
- CUSTOMER_SUPPORT

Reason:
One short sentence only.

Edge Cases:

1. Angry customer
→ Increase priority if urgent.

2. Very short message
→ Infer the most likely category.

3. Ambiguous ticket
→ Use GENERAL_SUPPORT.

Return exactly this JSON:

{
"category":"",
"priority":"",
"assignedTeam":"",
"reason":""
}

Support Ticket:

%s
""".formatted(ticket);

    }
}