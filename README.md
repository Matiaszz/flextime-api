# FlexTime API

A SaaS scheduling system with separate dashboards for companies and clients, built with Spring Boot.

## Features

- User and company registration & authentication (JWT-based, cookie storage)
- Role-based access control (CLIENT, WORKER, COMPANY)
- Appointment creation, update, deletion, and retrieval
- Company worker management
- Validation and global exception handling
- MySQL database integration
- Docker Compose support for local development

## Project Structure

- `src/main/java/dev/matias/flextime/api/` - Main application source code
- `src/main/resources/` - Application configuration files
- `src/test/java/` - Unit and integration tests
- `docker-compose.yaml` - Docker Compose file for MySQL setup
- `.env` / `.env-example` - Environment variable configuration

## Getting Started

### Prerequisites

- Java 21+
- Maven
- Docker (for local MySQL)

### Setup

1. Copy `.env-example` to `.env` and fill in your secrets and database credentials.
2. Start MySQL with Docker Compose:

   ```sh
   docker-compose up -d