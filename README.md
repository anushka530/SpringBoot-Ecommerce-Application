Here is the Markdown content of the `README.md` file from the repository:

```markdown
# E-Commerce Platform API

Scalable REST API backend for an e-commerce platform built with Spring Boot.

## About

Backend system handling user accounts, product catalog management, and order placement with secure endpoints and clean error handling.

## Features

- User registration, login and authentication via Spring Security
- Product catalog CRUD (create, read, update, delete)
- Order placement and management
- Bean Validation on all incoming requests
- Global exception handler for consistent error responses
- Admin vs Customer role separation

## Tech Stack

Java · Spring Boot · Spring Security · JPA · Hibernate · MySQL · Maven · Postman

## Architecture

Controller → Service → Repository (layered architecture)



 API runs on http://localhost:8080

## API Endpoints

| Method | Endpoint | Description | Auth |
| --- | --- | --- | --- |
| POST | /api/auth/register | Register user | No |
| POST | /api/auth/login | Login | No |
| GET | /api/products | Get all products | No |
| POST | /api/products | Add product | Admin |
| PUT | /api/products/{id} | Update product | Admin |
| DELETE | /api/products/{id} | Delete product | Admin |
| POST | /api/orders | Place order | User |
| GET | /api/orders | Get user orders | User |

```

```
