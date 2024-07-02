# Authentication and Audit Log System

## Project Overview
This project implements a proof of concept for an authentication and audit logging system. It leverages Scala with Akka HTTP for the backend, utilizing Doobie and PostgreSQL for database operations. It features user authentication, detailed audit logging, and email validation capabilities.

## Key Features
- **User Authentication:** Supports both standard username/password and Google OAuth methods.
- **Audit Logging:** Logs all CRUD operations with detailed information.
- **Email Validation:** Integrates with the ZeroBounce API for email verification.
- **Security:** Utilizes hashed passwords for secure data handling.

## Tech Stack
- **Scala** - Programming language.
- **Akka HTTP** - For building reactive web servers.
- **PostgreSQL** - Database.
- **Doobie** - For database interaction.
- **Cats & Cats Effect** - For functional programming.
- **Flyway** - For database migration.

## Project Structure
![image](https://github.com/rahulkhowal9239/Auth_Audit_POC/assets/74928000/5b5ab634-2611-446e-bc12-9916a7310df5)


## Setup and Running

### Open the Project
Open the project in IntelliJ IDEA or Visual Studio Code.

### Docker Setup
Navigate to the project directory and start the PostgreSQL server using Docker:
```bash
sudo docker-compose -f docker-compose.yml up

### Database Configuration
Open DBeaver and create a new database connection:
- **Username:** user
- **Password:** user
- **Port:** 5434

Manually create a new database named `auth_audit`.

### Build the Project
Compile the project using sbt:
```bash
sbt compile

## Run the Application

### Start the application:

```bash
sbt run

## Access the Application

The server will be available at `http://localhost:8080/`. Use this base URL to access the various endpoints provided by the application.

## API Documentation

- `POST /user`: Register a new user.
- `GET /user/{email}`: Retrieve a user by email.
- `PUT /user/{email}`: Update user details.
- `DELETE /user/{email}`: Delete a user.
- `GET /audit/logs`: Fetch audit logs.
- `POST /authenticated/user`: Register a user with Google authentication after email validation with ZeroBounce.
