<h1 align="center">EcommerceProject</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring%20AI-2.0.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/PostgreSQL-Database-316192?style=for-the-badge&logo=postgresql&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/Status-In%20Development-yellow?style=for-the-badge" />
</p>

<p align="center">
  A backend REST API for a full-featured e-commerce platform — built with Spring Boot. Proper layered architecture, JWT-based authentication, role-based access, product & cart management, and more in the pipeline.
</p>

---

## Overview

This is the **backend service** of a full-stack e-commerce application I'm building. Everything on the server side lives here — authentication, product management, cart, user addresses, AI-driven seller tools, and the security layer. A **React.js frontend** will be connected to this once the backend is complete.

The project started with just a category module. Over the development period, it has grown to include a proper auth system with JWT + Spring Security, product management with pagination and image uploads, a fully working cart, address management tied to logged-in users, and modern Spring AI integrations for smart product workflows.

Still going. Orders, payments, RAG customer support assistant, and Swagger docs are next.

---

## Project Structure

```
src/
└── main/
    ├── java/me/smmukesh/ecommerceproject/
    │   ├── config/              # App config, security config, Spring AI config, constants & data initializer
    │   ├── controller/          # REST controllers (Auth, Product, Cart, Category, Address, ProductAiController)
    │   ├── dto/
    │   │   ├── request/         # Incoming request DTOs (Auth, Address, AI description & banner requests)
    │   │   └── response/        # Outgoing response DTOs (API responses, AI analysis, banner, search, scraped images)
    │   ├── exception/           # Custom exceptions + global exception handler
    │   ├── model/               # JPA entity classes (User, Role, Category, Product, Cart, CartItem, Address)
    │   ├── repository/          # Spring Data JPA repositories
    │   ├── security/
    │   │   ├── jwt/             # JWT filter, entry point, and utilities
    │   │   ├── request/         # Auth request models (Login, Signup)
    │   │   ├── response/        # Auth response models
    │   │   └── service/         # UserDetailsImpl & UserDetailsServiceImpl
    │   ├── service/             # Business logic:
    │   │                        # - Core: CategoryService, ProductService, CartService, AddressService, FileService
    │   │                        # - AI: ProductAiService, ProductEmbeddingService, ProductImageAiService,
    │   │                        #       ProductBannerService, ProductImageScraperService
    │   └── utils/               # AuthUtils (gets logged-in user from context)
    └── resources/
        └── application.properties
```

> Follows the **Controller → Service → Repository** layered pattern throughout. Security and AI integrations are handled with dedicated modular services.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| **Spring Boot 4.1.0** | Core framework |
| **Java 21** | Language |
| **Spring Web MVC** | Building REST APIs |
| **Spring Data JPA + Hibernate** | ORM & database interaction |
| **PostgreSQL** | Primary database |
| **Spring Security** | Authentication & authorization |
| **JJWT 0.12.6** | JWT token generation & validation |
| **ModelMapper 3.2.4** | Entity to DTO mapping |
| **Bean Validation (Jakarta)** | Request body validation |
| **Lombok** | Reducing boilerplate code |
| **Spring AI 2.0.1** | OpenAI integration (ChatClient, Vector Store, Image Models) |
| **Maven** | Build & dependency management |

---

## Authentication & Security

The app uses **stateless JWT-based authentication** with cookies.

- Tokens are generated on login and sent via `HttpOnly` cookies (`springBootEcommerce`)
- Every protected request goes through `AuthTokenFilter` which extracts and validates the JWT before hitting the controller
- Unauthorized requests are handled by `AuthEntryPoint` which returns a clean `401` JSON response
- Passwords are hashed with **BCrypt**
- Three roles are supported: `ROLE_USER`, `ROLE_SELLER`, `ROLE_ADMIN`
- Role data is seeded automatically at startup via `DataInitializer`

---

## API Endpoints

### Auth — `/api/auth`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/api/auth/signup` | Register a new user | Public |
| `POST` | `/api/auth/signin` | Login & get JWT cookie | Public |
| `POST` | `/api/auth/signout` | Clear JWT cookie | Public |
| `GET` | `/api/auth/username` | Get current username | Authenticated |
| `GET` | `/api/auth/user` | Get current user details | Authenticated |

### Categories — `/api`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/api/public/categories` | List all categories | Authenticated |
| `POST` | `/api/public/category` | Create a category | Authenticated |
| `PUT` | `/api/public/categories/{categoryId}` | Update a category | Authenticated |
| `DELETE` | `/api/admin/categories/{id}` | Delete a category | Admin |

### Products — `/api`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/api/admin/categories/{categoryId}/product` | Add product to a category | Admin |
| `GET` | `/api/public/products` | Get all products (paginated) | Authenticated |
| `GET` | `/api/public/categories/{categoryId}/products` | Products by category (paginated) | Authenticated |
| `GET` | `/api/public/products/keyword/{keyword}` | Search products by keyword | Authenticated |
| `PUT` | `/api/admin/products/{productId}` | Update a product | Admin |
| `DELETE` | `/api/admin/products/{productId}` | Delete a product | Admin |
| `PUT` | `/api/admin/products/{productId}/image` | Upload/update product image | Admin |

### Cart — `/api`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/api/carts/products/{productId}/quantity/{quantity}` | Add item to cart | Authenticated |
| `GET` | `/api/carts` | Get all carts (admin view) | Authenticated |
| `GET` | `/api/carts/users/cart` | Get logged-in user's cart | Authenticated |
| `PUT` | `/api/cart/product/{productId}/quantity/{operation}` | Increase or decrease item quantity | Authenticated |
| `DELETE` | `/api/cart/{cartId}/product/{productId}` | Remove item from cart | Authenticated |

### Addresses — `/api`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/api/addresses` | Save a new address | Authenticated |
| `GET` | `/api/addresses` | Get all addresses | Authenticated |
| `GET` | `/api/addresses/{addressId}` | Get address by ID | Authenticated |
| `GET` | `/api/users/addresses` | Get logged-in user's addresses | Authenticated |
| `PUT` | `/api/addresses/{addressId}` | Update an address | Authenticated |
| `DELETE` | `/api/addresses/{addressId}` | Delete an address | Authenticated |

### AI Services — `/api`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/api/admin/products/generate-description` | Generate product description from details using AI | Admin |
| `POST` | `/api/admin/products/{productId}/generate-description` | Generate description for existing product | Admin |
| `GET` | `/api/public/products/search/semantic` | Semantic product search using vector embeddings | Public |
| `POST` | `/api/admin/products/sync-embeddings` | Sync products to vector store | Admin |
| `POST` | `/api/admin/products/analyze-image` | Analyze product image with vision model | Admin |
| `POST` | `/api/admin/products/{productId}/analyze-image` | Analyze existing product image | Admin |
| `POST` | `/api/admin/products/generate-banner` | Generate marketing banner using image model | Admin |
| `POST` | `/api/admin/products/{productId}/generate-banner` | Generate banner for existing product | Admin |
| `POST` | `/api/admin/products/scrape-images` | Scrape product photos from web | Admin |
| `POST` | `/api/admin/products/{productId}/attach-scraped-images` | Download and attach images to product gallery | Admin |

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL running locally

### Database Setup

Create a PostgreSQL database before running the app:

```sql
CREATE DATABASE "Ecommerce";
```

Then update `application.properties` if your PostgreSQL credentials are different:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/Ecommerce
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Running Locally

```bash
# Clone the repository
git clone https://github.com/mukesh-sm-fsjwd/ecommerce-springboot.git

# Navigate into the project
cd ecommerce-springboot

# Run using Maven wrapper
./mvnw spring-boot:run
```

> On Windows: `mvnw.cmd spring-boot:run`

The server starts at **`http://localhost:8080`**.

---

## Key Design Decisions

### DTO Layer
All request and response data goes through dedicated DTOs — entities never leave the service layer raw. `ModelMapper` handles the conversion between entity and DTO to keep things clean and maintainable.

### Pagination & Sorting
Product listings support pagination and sorting out of the box via query params:

```
GET /api/public/products?pageNumber=0&pageSize=10&sortBy=price&sortOrder=asc
```

Default values are managed centrally in `AppConstants`.

### Product Image Uploads
Product images are handled via `MultipartFile` uploads and stored under the `images/` directory. The path is configurable via `project.image` in `application.properties`.

### Auth Context Utility
`AuthUtils` is a small helper that pulls the currently authenticated user's email or full `User` object from the Spring Security context — used across controllers like Cart and Address to scope data to the logged-in user without passing tokens around manually.

### Exception Handling
`GlobalExceptionHandler` (`@RestControllerAdvice`) catches:
- `MethodArgumentNotValidException` — returns a field-level error map (`400`)
- `ResourceNotFoundException` — returns a descriptive `404` message
- `APIException` — handles operational errors and model response issues

### AI Architecture & Spring AI Integration
Spring AI 2.0.1 is integrated into the backend architecture to support practical e-commerce workflows without compromising core business logic:
- **ChatClient with Structured Converter**: Uses `PromptTemplate` and `BeanOutputConverter` to produce structured JSON containing taglines, bullet points, and descriptions from seller inputs.
- **Vector Search Engine**: Implements an in-memory vector store with OpenAI text embeddings to enable semantic product discovery alongside keyword search.
- **Multimodal Image Inspector**: Accepts uploaded product photos and analyzes visual details to auto-suggest titles, categories, and key features.
- **Promotional Banner Generator**: Connects to OpenAI image generation models to create promotional campaign graphics on demand.
- **Web Photo Scraper & Disk Storage**: Scrapes clean product photos from the web and decodes them directly into the local `images/` directory and `product_images` database table.

---

## Data Models

| Entity | Key Fields |
|---|---|
| `User` | `userId`, `userName`, `email`, `password`, `roles`, `addresses`, `cart` |
| `Role` | `roleId`, `roleName` (ROLE_USER / ROLE_SELLER / ROLE_ADMIN) |
| `Category` | `categoryId`, `categoryName` |
| `Product` | `productId`, `productName`, `description`, `price`, `discount`, `specialPrice`, `quantity`, `image`, `images`, `category` |
| `Cart` | `cartId`, `user`, `cartItems`, `totalPrice` |
| `CartItem` | `cartItemId`, `cart`, `product`, `quantity`, `discount`, `productPrice` |
| `Address` | `addressId`, `street`, `buildingName`, `city`, `state`, `country`, `pincode`, `user` |

---

## Roadmap

### Completed
- [x] Category CRUD
- [x] Global exception handling & bean validation
- [x] Product management with image upload, pagination & keyword search
- [x] User registration & login with JWT authentication
- [x] Role-based access control (USER / SELLER / ADMIN)
- [x] Shopping cart (add, update quantity, remove items)
- [x] Address management (linked to logged-in user)
- [x] Migrated from H2 to PostgreSQL
- [x] Stateless session management (no server-side sessions)
- [x] AI Product Description Generator (Spring AI ChatClient & Structured Output)
- [x] AI Semantic Product Search (Vector Store & Embeddings)
- [x] Multimodal Vision Inspector (Product image auto-fill)
- [x] AI Marketing Banner Generator (OpenAI Image Model)
- [x] Online Product Image Scraper & Multi-image Gallery persistence

### Upcoming
- [ ] **RAG Customer Support Assistant** — conversational store support with chat memory and vector retrieval
- [ ] **Order & Payments Module** — checkout, order tracking, and payment processing
- [ ] **Swagger / OpenAPI Documentation** — interactive API documentation at `/swagger-ui`
- [ ] **React.js Frontend** — complete user interface integrated with all backend REST endpoints
- [ ] **AWS Deployment** — backend hosting on EC2 / Elastic Beanstalk with RDS PostgreSQL

---

## Contributing

This is a personal learning project, but if you spot something off or have a suggestion, feel free to open an issue or a PR. Always open to feedback.

---

## Author

**Mukesh SM**
- GitHub: [@mukesh-sm-fsjwd](https://github.com/mukesh-sm-fsjwd)

---

<p align="center">Built with Java, Spring Boot, and a lot of Stack Overflow tabs open.</p>
