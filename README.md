# Banking Management System — Spring Boot

Educational REST API using **Java 17, Spring Boot 4, Spring Data JPA, Bean Validation, H2, and JUnit 5**.

## Capabilities
- Create accounts with nonnegative starting balance and unique account number
- Find accounts by ID and list accounts
- Deposit and withdraw positive amounts, refusing overdrafts
- Transfer between different accounts with one database transaction
- Pessimistic database row locking (consistent account ID order) to reduce lost updates/deadlocks
- Request/response DTOs rather than exposing JPA entities
- Consistent errors for invalid inputs (400), missing accounts (404), and conflicts (409)
- Integration and validation tests; GitHub Actions Maven verification

## Run
Requires Java 17+ and Maven (or the included Maven wrapper).

```bash
./mvnw test
./mvnw spring-boot:run
```

Windows: `mvnw.cmd test` and `mvnw.cmd spring-boot:run`.

The API runs at `http://localhost:8080`. The project includes Spring Security; with its current default setup, requests require authentication. For local testing, Spring Security prints a generated development password for user `user` at startup unless a username/password is configured. Do not deploy this demo for real financial transactions. Production use would require TLS, per-account authorization, audit logging, fraud controls and much stronger authentication.

## API examples
| HTTP | Endpoint | Request body |
|---|---|---|
| POST | `/api/accounts` | `{"ownerName":"Alice","accountNumber":"NL001","initialBalance":100.00}` |
| GET | `/api/accounts` | none |
| GET | `/api/accounts/{id}` | none |
| POST | `/api/accounts/{id}/deposit` | `{"amount":25.00}` |
| POST | `/api/accounts/{id}/withdraw` | `{"amount":10.00}` |
| POST | `/api/accounts/transfer` | `{"fromAccountId":1,"toAccountId":2,"amount":50.00}` |

Example using HTTP Basic auth:
```bash
curl -u user:YOUR_LOCAL_PASSWORD -H "Content-Type: application/json" \
  -d '{"ownerName":"Alice","accountNumber":"NL001","initialBalance":100.00}' \
  http://localhost:8080/api/accounts
```

H2 runs **in memory**; data does not persist across app restarts. The H2 console and debug configuration are for local development only.

## Quality checks
Run `./mvnw verify` to compile and execute unit/integration tests. GitHub Actions runs the same check on pushes and pull requests.

**Note:** This is a portfolio/demo project, not a real banking system.
