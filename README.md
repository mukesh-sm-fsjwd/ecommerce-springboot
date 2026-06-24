<h1 align="center">🛒 EcommerceProject</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/H2-Database-blue?style=for-the-badge&logo=h2&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/Status-In%20Development-yellow?style=for-the-badge" />
</p>

<p align="center">
  A backend REST API for a full-featured e-commerce platform — built from scratch with Spring Boot. Clean architecture, proper validation, global exception handling, and more to come.
</p>

---

## 📌 What's This About?

This is the **backend service** of an e-commerce web application I'm building. The plan is to have a full-stack app — this repo handles all the server-side logic (REST APIs, data persistence, validation, etc.), and a React.js frontend will be wired up to it later.

Right now, the project is in active development. The foundation is solid — layered architecture, JPA-based persistence, in-memory H2 database for quick development, and a global exception handler so errors never just vanish silently.

---

## 🏗️ Project Structure

```
src/
└── main/
    ├── java/me/smmukesh/ecommerceproject/
    │   ├── controller/          # REST controllers — entry point for all HTTP requests
    │   ├── model/               # JPA entity classes
    │   ├── repository/          # Spring Data JPA repositories
    │   ├── service/             # Business logic layer
    │   └── exception/           # Custom exceptions + global handler
    └── resources/
        └── application.properties
```

> The architecture follows the standard **Controller → Service → Repository** pattern. Each layer has one job and does it well.

---

## ⚙️ Tech Stack

| Technology | Purpose |
|---|---|
| **Spring Boot 4.1.0** | Core framework |
| **Java 21** | Language |
| **Spring Web MVC** | Building REST APIs |
| **Spring Data JPA** | Database interaction (ORM) |
| **Hibernate** | JPA implementation |
| **H2 (In-Memory DB)** | Development database |
| **Bean Validation (Jakarta)** | Request body validation |
| **Lombok** | Reducing boilerplate code |
| **Maven** | Build & dependency management |

---

## 🔌 API Endpoints

### Categories

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/public/categories` | Fetch all categories |
| `POST` | `/api/public/category` | Create a new category |
| `PUT` | `/api/public/categories/{categoryId}` | Update an existing category |
| `DELETE` | `/api/admin/categories/{id}` | Delete a category (admin only) |

> **Note:** Endpoints under `/api/public/` are open. Endpoints under `/api/admin/` are intended for admin use (auth/security will be added in upcoming iterations).

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:

- Java 21+
- Maven 3.8+

### Running Locally

```bash
# Clone the repository
git clone https://github.com/mukesh-sm-fsjwd/ecommerce-springboot.git

# Navigate into the project
cd ecommerce-springboot

# Run using Maven wrapper
./mvnw spring-boot:run
```

> On Windows, use `mvnw.cmd spring-boot:run` instead.

The server starts on **`http://localhost:8080`** by default.

### H2 Console

Since this project uses an in-memory H2 database, you can browse your data directly at:

```
http://localhost:8080/h2-console
```

| Setting | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:test` |
| Username | `sa` |
| Password | *(leave empty)* |

---

## 🧠 Key Design Decisions

### Validation
Every incoming request body goes through **Jakarta Bean Validation**. For example, a category name can't be blank and must be at least 5 characters long. If validation fails, the global exception handler catches it and returns a clean, structured error response instead of a generic 500.

### Exception Handling
A `@RestControllerAdvice` class (`GlobalExceptionHandler`) handles:
- **`MethodArgumentNotValidException`** — returns field-level validation errors as a map (`{ "fieldName": "error message" }`)
- **`ResourceNotFoundException`** — returns a `404 Not Found` with a descriptive message when a resource (e.g., category by ID) doesn't exist

### Constructor Injection
All dependencies are injected through **constructor injection** instead of field injection — this makes testing easier and keeps things explicit.

---

## 🗂️ Data Model

### `Category`

```java
@Entity(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank
    @Size(min = 5)
    private String categoryName;
}
```

Simple and clean. More entities (Product, Order, User, etc.) are on the roadmap.

---

## 🗺️ Roadmap

These are things I'm planning to build out as the project grows:

- [x] Category CRUD operations
- [x] Global exception handling
- [x] Bean validation
- [ ] Product management (add, update, delete, filter by category)
- [ ] User authentication & authorization (Spring Security + JWT)
- [ ] Order & cart management
- [ ] Role-based access control (ADMIN / USER)
- [ ] Switch to PostgreSQL / MySQL for production
- [ ] React.js frontend integration
- [ ] API documentation with Swagger/OpenAPI

---

## 🤝 Contributing

This is a personal learning project, but if you spot something off or have a suggestion, feel free to open an issue or drop a PR. I'm always open to feedback.

---

## 👨‍💻 Author

**Mukesh SM**
- GitHub: [@mukesh-sm-fsjwd](https://github.com/mukesh-sm-fsjwd)

---

<p align="center">
  Built with ☕ and a lot of Stack Overflow tabs open.
</p>
