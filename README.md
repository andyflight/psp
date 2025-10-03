# Payment Service Provider (PSP)

This repository contains a simple implementation of a Payment Service Provider (PSP) using Java and Spring Boot. The PSP simulates receiving the transaction request and mock the processing.
Project demonstrates the use of Spring WebFlux for reactive programming, Lombok for boilerplate code reduction, and Jakarta Validation for input validation.
The architecture is designed with DDD principles in mind, separating concerns into different layers.

### Stack
- Java 21
- Spring Boot 3.5.6
- Gradle 8.14.3
- Spring WebFlux
- Lombok
- Jakarta Validation

### Why this design
- Separation of concerns: domain core doesn’t depend on frameworks; adapters plug in later.
- Testability: time is injected via Clock, value objects validate invariants on creation, and ports allow mocking.
- Evolvability: adding new acquirers or switching persistence does not change the domain core.
- Reactive: using WebFlux allows handling many concurrent requests efficiently.
- Validation: Jakarta Validation ensures input data integrity.
- Lombok: reduces boilerplate code for data classes.

### Architecture
- Hexagonal principles
    - Ports: domain-driven interfaces exposing functionality (TransactionService, Acquirer, AcquirerRouter, TransactionRepository)
    - Adapters: implementations of ports (TransactionServiceImpl, AcquirerA and AcquirerB, AcquirerRouterImpl, InMemoryTransactionRepository)
- Domain-Driven-Design (DDD)
    - Entities: core business objects with identity (Transaction)
    - Value Objects: immutable objects representing concepts (Money, CardDetails, StoredCardInfo)
    - enums: fixed sets of constants (TransactionStatus, AcquirerType, AcquirerDecision)
- WebFlux
    - Functional routing: defining routes using RouterFunction (TransactionRouterFunction)
    - Reactive handlers: handling requests reactively (TransactionHandler)
    - Reactive types: using Mono and Flux for async processing
    - exception handling: centralized error handling (GlobalExceptionHandler, GlobalErrorAttributes)
- Patterns used:
    - Factory/static initializers enforce invariants (Transaction.initialize, StoredCardInfo.of)
    - Builder using Lombok on top of value objects (Money, CardDetails) and application objects (PaymentRequest, PaymentResponse)
    - Factory class for acquirer selection (AcquirerRouter)
    - Strategy pattern for acquirer implementations (AcquirerA, AcquirerB)
    - Dependency Injection via Spring for wiring components together
    - Repository pattern for data access abstraction (TransactionRepository)

### Project Structure
```text
.
├── api/
│   ├── dto/
│   │   └── transaction
│   ├── handler/
│   │   └── TransactionHandler
│   └── router/
│       └── TransactionRouterFunction
├── config/
│   └── ClockConfig
├── domain/
│   ├── entities/
│   │   └── Transaction
│   ├── enums/
│   │   ├── AcquirerDecision
│   │   ├── AcquirerType
│   │   └── TransactionStatus
│   └── valueobjects/
│       ├── StoredCardInfo
│       ├── Money
│       └── CardDetails
├── exception/
│   └── handler/
│       ├── GlobalExceptionHandler
│       └── GlobalErrorAttributes
├── repository/
│   └── transaction/
│       ├── impl/
│       │   └── InMemoryTransactionRepositoryImpl
│       └── TransactionRepository
├── service/
│   ├── acquirer/
│   │   ├── impl/
│   │   │   ├── AcquirerA
│   │   │   ├── AcquirerB
│   │   │   └── AcquirerRouterImpl
│   │   └── ports/
│   │       ├── Acquirer
│   │       └── AcquirerRouter
│   └── transaction/
│       ├── impl/
│       │   └── TransactionServiceImpl
│       └── ports/
│           ├── PaymentRequest
│           ├── PaymentResponse
│           └── TransactionService
├── shared/
│   └── CardValidation
└── PspApplication
```

#### Running the Application
1. Ensure you have Docker and Docker Compose installed.
2. Clone the repository
3. Navigate to the project directory.
4. Build and run the application using Docker Compose:
   ```bash
   docker-compose up --build
   ```
5. The application will be accessible at `http://localhost:8080`.
