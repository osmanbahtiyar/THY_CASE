# THY Transportation Management System

Transportation management system built with Spring Boot backend and React frontend.

For the <strong>Postman</strong> collection, you can use the postman collection file in the files folder and import it to your postman.

## 🏗️ Architecture

This project follows **Hexagonal Architecture** principles with clear separation of concerns:

- **Domain Layer**: Core business logic and entities
- **Infrastructure Layer**: External concerns (database, web, etc.)

Global error handling is implemented using Spring Boot’s `@RestControllerAdvice`, ensuring centralized exception handling and consistent API error responses.

### Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.5.6
- PostgreSQL
- Maven
- Lombok
- Flyway
- Mapstruct

**Frontend:**
- React
- Chakra UI
- TypeScript
- Vite

**Database:**
- PostgreSQL 16+

## 🚀 Running the Application

To run the application, you need to have Docker installed or have a local PostgreSQL database.

```bash
cd database
docker compose up 
```

Database comes with some predefined data in the database.

## 📦 Backend
To access the backend, you can use the swagger ui.

```bash
http://localhost:8080/swagger-ui.html
```