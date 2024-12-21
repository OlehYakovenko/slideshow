
# Slideshow Management Application

A Java-based application for managing slideshows and their associated images, built with **Spring Boot**. The application supports image validation, slideshow creation, proof-of-play recording, and logging using Kafka.

## Features

- **Image Management**: Add, delete, and search images with validation.
- **Slideshow Management**: Create and manage slideshows with multiple images.
- **Kafka Integration**: Logs actions (e.g., add/delete image, slideshow) to Kafka topics.
- **Database Support**: Uses PostgreSQL for persistent storage.
- **Validation**: Validates image URLs and content type.
- **REST API**: Exposes RESTful endpoints for interacting with the system.

---

## Table of Contents

1. [Tech Stack](#tech-stack)
2. [Project Structure](#project-structure)
3. [Getting Started](#getting-started)
    - [Requirements](#requirements)
    - [Local Setup](#local-setup)
4. [Usage](#usage)
5. [API Endpoints](#api-endpoints)
6. [Testing](#testing)
7. [Docker and Docker Compose](#docker-and-docker-compose)

---

## Tech Stack

- **Java** (OpenJDK 17)
- **Spring Boot** (Web, Data JPA, Kafka)
- **PostgreSQL** (Database)
- **Kafka** (Logging events)
- **Lombok** (For reducing boilerplate code)
- **JUnit 5** and **Mockito** (For unit and integration tests)
- **WebClient** (For HTTP requests)

---

## Project Structure

```
my-project/
├── src/
│   ├── main/
│   │   ├── java/com/practice/slideshow/  # Main source code
│   │   ├── resources/                    # Configuration files
│   ├── test/                             # Unit and integration tests
├── Dockerfile                            # Docker containerization
├── docker-compose.yml                    # Local setup with database and Kafka
├── pom.xml                               # Maven dependencies
└── README.md                             # Project documentation
```

---

## Getting Started

### Requirements

- **Java 17** or higher
- **Maven** (3.6+)
- **Docker** and **Docker Compose**
- **Kafka** (via Docker Compose or external setup)

### Local Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-repository/slideshow-management.git
   cd slideshow-management
   ```

2. **Build the application**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Start Docker services**:
   Use Docker Compose to spin up PostgreSQL and Kafka:
   ```bash
   docker-compose up
   ```

---

## Usage

- Base URL: `http://localhost:8080`

Use tools like **Postman** or **cURL** to interact with the API.

---

## API Endpoints

### Image Endpoints

- **Add Image**: `POST /image`
- **Delete Image**: `DELETE /image/{id}`
- **Search Images**: `GET /image/search?keyword={keyword}`

### Slideshow Endpoints

- **Add Slideshow**: `POST /slideshow`
- **Delete Slideshow**: `DELETE /slideshow/{id}`
- **Get Slideshow Order**: `GET /slideshow/{id}/order`

---

## Testing

Run unit and integration tests with:
```bash
mvn test
```

### Embedded Kafka Tests

Kafka integration tests are included in the project. These use the `@EmbeddedKafka` annotation to spin up a test Kafka broker.

---

## Docker and Docker Compose

### Build Docker Image

```bash
docker build -t slideshow-app .
```

### Run with Docker Compose

The `docker-compose.yml` file sets up the application along with Kafka and PostgreSQL.

```bash
docker-compose up
```

Services:
- **slideshow-app**: Application container (exposes port 8080)
- **kafka**: Kafka broker
- **postgres**: PostgreSQL database

---

## Troubleshooting

### Common Issues

- **Kafka Connection Issues**: Ensure Kafka is running locally or in Docker.
- **Database Connection Issues**: Verify PostgreSQL is running and accessible.

### Logs

Check application logs for detailed error messages:
```bash
docker logs slideshow-app
```

---

## License

This project is licensed under the MIT License. See `LICENSE` for details.

---

## Author

- **Oleh Yakovenko**
