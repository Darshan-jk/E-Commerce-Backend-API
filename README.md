# E-Commerce REST API

A backend E-Commerce REST API built using Spring Boot, MySQL, Spring Security, and JWT authentication.

## Features

- User registration and login
- JWT authentication
- USER and ADMIN roles
- Product management
- Category management
- Inventory management
- Shopping cart
- Address management
- Order management
- Payment management
- Input validation
- Global exception handling

## Technologies Used

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT
- MySQL
- Maven
- Lombok
- Postman

## Project Structure

```text
src/
└── main/
    ├── java/com/project/ecommerce/
    │   ├── config/
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── enums/
    │   ├── exception/
    │   ├── repository/
    │   ├── security/
    │   └── service/
    │
    └── resources/
        └── application.properties

pom.xml
```

## Main API Modules

```text
/api/auth
/api/users
/api/products
/api/categories
/api/inventory
/api/cart
/api/addresses
/api/orders
/api/payments
```

## How to Run

### 1. Clone the repository

```bash
git clone https://github.com/Darshan-jk/E-Commerce-Backend-API
```

### 2. Configure MySql

Create a database:

```sql
CREATE DATABASE ecommerce_db;
```

Update `application.properties` with your MySql username and password.

### 3. Run the application

```bash
mvn spring-boot:run
```

The API will run at:

```text
http://localhost:8080
```

## Testing

The APIs can be tested using Postman.

Typical flow:

```text
Register
   ↓
Login
   ↓
Get Products
   ↓
Add Product to Cart
   ↓
Add Address
   ↓
Place Order
   ↓
Create Payment
   ↓
Complete Payment
```

## Authentication

Protected APIs require a JWT token:

```text
Authorization: Bearer <your-jwt-token>
```

## Payment

The project contains a simple payment simulation for learning purposes. It does not use a real payment gateway.

## Author

**Darshan JK**
