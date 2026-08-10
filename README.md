Banking Management System (Spring Boot)

A Spring Boot backend for managing customer bank accounts, built with a layered architecture (Controller → Service → Repository) and an in-memory H2 database.

Features
Create a new bank account with owner name, account number, and initial balance
Validation to prevent negative initial balances
Retrieve all accounts
Retrieve a single account by ID
Custom exception handling for accounts that don't exist
Tech Stack
Java 21
Spring Boot 3.3
Spring Data JPA / Hibernate
H2 (in-memory database)
Maven
Project Structure
entity/      → Account (JPA entity)
dto/         → AccountRequest (incoming request payload)
repository/  → AccountRepository (JPA repository)
service/     → AccountService (business logic)
controller/  → AccountController (REST endpoints)
exception/   → ResourceNotFoundException
How to Run
bash
mvn clean install
mvn spring-boot:run

The API starts on http://localhost:8080. H2 console available at http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:bankdb).

API Overview
Method	Endpoint	Description
POST	/api/accounts	Create a new account
GET	/api/accounts	List all accounts
GET	/api/accounts/{id}	Get a single account by ID
Example Request
json
POST /api/accounts
{
  "ownerName": "Ali Rezaei",
  "accountNumber": "1001",
  "initialBalance": 500
}
Roadmap
Deposit / withdraw endpoints
Transfer between accounts with @Transactional
Global exception handler returning proper HTTP status codes (e.g. 404 for not found)
Author

Mohaddeseh Akmali — LinkedIn
