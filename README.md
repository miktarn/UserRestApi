# UserRestApi
This project is a User REST API developed as part of a test assignment for "Clever Solutions" company. The project includes endpoints for creating, reading, updating, and deleting user information.

## Test Coverage

![Test Coverage](/test-coverage-screenshot.png)

The controller and service classes in this project have 100% test coverage.

## Dependencies and Plugins

This project uses a variety of technologies and dependencies, some of which are detailed below:

- **Spring Boot Starter Web and Spring JPA**
- **H2 Database**
- **MapStruct and Lombok**
- **Hibernate Validator**
- **JUnit 5, Mockito, Parameterized tests**
- **Checkstyle Plugin**: Enforces coding standards and checks the source code against a style guide.

## Restful endpoints

| Endpoint Description               | URL           | HTTP Method | Success Code |
|------------------------------------|---------------|-------------|--------------|
| Create User                        | `/users`      | POST        | 201 Created  |
| Get Users by Birth Date Range      | `/users`      | GET         | 200 OK       |
| Update User                        | `/users/{id}` | PUT         | 200 OK       |
| Partial Update User                | `/users/{id}` | PATCH       | 200 OK       |
| Delete User                        | `/users/{id}` | DELETE      | 204 No Content |

## Other

- implemented CustomGlobalExceptionHandler and custom exception EntityNotFoundException
- implemented custom validation annotations and validators for them
- all requests and responses performed using DTO handled by Mapstruct 

## Contact Me

Telegram: https://t.me/miktarn
Email: miktarnavskyi@gmail.com