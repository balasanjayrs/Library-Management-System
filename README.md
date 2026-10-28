# Library Management System

A simple, responsive Spring Boot and MySQL mini-project for cataloguing books, registering members, issuing and returning books, tracking loans and calculating overdue fines.

## Features

- Secured dashboard, book/member CRUD, 14-day issue loans, return workflow, fine assessment and overdue reporting
- MySQL persistence through Spring Data JPA
- Thymeleaf pages and a responsive light-green interface
- BCrypt-hashed administrator credentials
- Server-side validation and understandable flash messages
- Transactional issue/return operations and active-loan deletion protection
- Fine: `₹5 × max(0, returnDate − dueDate)`; fine is assessed on return and remains unpaid

## Requirements

- JDK 21
- Maven 3.9+
- MySQL 8+
- macOS/Linux/Windows shell

## Database setup

1. Start MySQL.
2. Create a database and least-privilege application user:

```sql
CREATE DATABASE library_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'library_user'@'localhost' IDENTIFIED BY 'choose-a-strong-password';
GRANT SELECT, INSERT, UPDATE, DELETE ON library_management.* TO 'library_user'@'localhost';
FLUSH PRIVILEGES;
```

The optional `src/main/resources/db/schema.sql` shows the equivalent JPA tables. JPA creates or updates them with `ddl-auto=update`, so it is not necessary to run the script. For a fresh database, run the script and provide the user through environment variables.

## Configure credentials safely

Never commit database credentials or passwords. Use environment variables:

```bash
export DB_URL='jdbc:mysql://localhost:3306/library_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DB_USERNAME='library_user'
export DB_PASSWORD='choose-a-strong-password'
export INITIAL_ADMIN_USERNAME='your-admin-name'
export INITIAL_ADMIN_PASSWORD='choose-a-strong-admin-password'
```

`INITIAL_ADMIN_*` is required only for the first start. The initializer creates one BCrypt-hashed admin and then does nothing. Remove these variables after the initial account exists. For IDE configuration, copy `src/main/resources/application-example.properties` to `application-local.properties` and pass `--spring.config.additional-location=application-local.properties` without committing that file.

## Run

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"  # adjust for your JDK location
mvn spring-boot:run
```

Open `http://localhost:8080`. Sign in with the initial admin credentials you supplied once.

Package and run:

```bash
mvn clean package
java -jar target/library-management-1.0.0.jar
```

## Tests

```bash
mvn test
```

The service tests cover book/member creation and search, available-book issue rules, 14-day due dates, return behavior, zero/nonzero fines, repeated return rejection and overdue detection. H2 is test-only and does not replace MySQL in production.

## Project structure

```text
src/main/java/com/library/
├── config/       Security and initial admin setup
├── controller/   Web routes and form binding
├── exception/    Domain errors
├── model/        JPA entities and enums
├── repository/   Spring Data repositories
└── service/      Transactional library business rules
src/main/resources/
├── db/schema.sql
├── static/css/js/
└── templates/    Thymeleaf pages and layout
src/test/java/com/library/service/
```

## Verification and limitations

- Generated numeric IDs are database identifiers; uniqueness and foreign keys are enforced by MySQL/JPA.
- A final, end-to-end browser run requires MySQL and the credentials above.
- Fine payment is intentionally not tracked or marked paid automatically; the system distinguishes assessed fine from amount unpaid.
- Deletion is blocked while an active loan references a book or member. Completed loan history is retained.
- The initial admin bootstrap fails safely when no account exists and credentials are not configured; it never ships public credentials.
