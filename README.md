# Banking Application

This is a microservices-based banking application built with Spring Boot. It demonstrates a modular architecture with multiple services handling specific responsibilities, integrated through an API gateway and service registry.

## 💡 Features

- Modular microservices architecture
- User registration and authentication using JWT
- Transaction management between accounts
- Centralized API Gateway for routing requests
- Service discovery using Eureka
- Inter-service communication using Feign Clients
- Input validation and global exception handling
- Secure endpoints with Spring Security

## 📦 Services

- `banking-api-gateway` — Central API entry point for routing requests to services
- `banking-service-registry` — Service discovery using Spring Cloud Eureka
- `online-banking-service` — Core banking operations and logic
- `transaction-service` — Handles fund transfers and transaction history
- `user-detail-service` — Manages user profiles and account details
- `banking-security-service` — Handles login, authentication, and JWT token management

## 🛠️ Technologies Used

- Java
- Spring Boot
- Spring Cloud (Eureka, OpenFeign)
- Spring Security
- JWT (JSON Web Tokens)
- Maven
- Bean Validation (Hibernate Validator)

## 🚀 How to Run

```bash
mvn clean install
mvn spring-boot:run
