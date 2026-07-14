package com.example.smart_ticket_router.prompt;

public class PromptBuilder {

    public static String buildPrompt(String ticket) {

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