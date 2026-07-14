# Smart Ticket Router

## Overview

Smart Ticket Router is a Spring Boot application that uses OpenAI to automatically classify customer support tickets.

## Features

- AI-powered ticket classification
- Ticket priority prediction
- Automatic team assignment
- Reason generation
- REST API
- Thymeleaf UI

## Technology Stack

- Java 21
- Spring Boot 4.1
- Maven
- Thymeleaf
- Bootstrap 5
- OpenAI API
- Jackson

## Project Structure

client/
controller/
enums/
model/
prompt/
service/
templates/

## Running the Project

1. Add your OpenAI API key to `application.properties`

2. Run:

mvn spring-boot:run

3. Open:

http://localhost:8080

## Sample API

POST /api/route

```json
{
  "message":"I forgot my password."
}
```

Example Response

```json
{
  "category":"AUTHENTICATION",
  "priority":"MEDIUM",
  "assignedTeam":"ACCOUNT_SUPPORT",
  "reason":"Password reset assistance required."
}
```
                 User

                  │

                  ▼

        Thymeleaf Web Page

                  │

                  ▼

       TicketWebController

                  │

                  ▼

      TicketRoutingService

                  │

                  ▼

          PromptBuilder

                  │

                  ▼

           OpenAI Client

                  │

                  ▼

          OpenAI GPT Model

                  │

                  ▼

         JSON Ticket Response

                  │

                  ▼

         Display to the User