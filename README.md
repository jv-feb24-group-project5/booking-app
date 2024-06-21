
# Accommodation Service Web Application

![Project Logo](images/student-accommodation.jpg)

## Introduction 📜

Welcome to the Accommodation Service Web Application!
This project was inspired by the need for a comprehensive platform that allows users to browse,
purchase, and manage bookings online. It aims to provide a seamless experience for both users
and administrators. Users can browse bookings by status, receive notifications about actions,
and create new bookings, while administrators have the ability to manage the accesses and accommodations.

## Technologies and Tools Used 🛠️

This project leverages a range of modern technologies and tools to ensure robust performance and security:

- **Spring Boot**: For building the backend of the application.
- **Spring Security**: To handle authentication and authorization.
- **Spring Data JPA**: For database operations.
- **Swagger**: To document and test the APIs.
- **Liquibase**: For database version control.
- **Docker**: To containerize the application for easy deployment.
- **MapStruct**: For object mapping.
- **Stripe**: For taking payment from users.
- **Telegram**: For sending notifications to users.

## Features 🌟

### User Functionalities 🥺
- **Create Booking**: Users can create and manage their bookings.
- **User Manage**: Users can check info about their profiles and update it.
- **View Accommodations**: Users can view all accommodations or specific by id.

### Admin Functionalities 😊
- **Roles Access**: Admins can update roles for users
- **Manage Bookings**: Admins can browse all bookings by status
- **Manage Accommodation**: Admins can create, update and delete accommodations

## Setup Instructions 🧙‍♂️🔮

To set up the project locally, follow these steps:

### Prerequisites

Ensure you have the following software installed:

- **Java 21**
- **Docker Version 4.30.0 (149282)**
- **Maven 3.9.5**

### Database Configuration

You can use either Postgres or H2 database.
For simplicity, the following instructions will use H2.
If you prefer Postgres, ensure you update the necessary configuration in `application.properties`.

### Environment Variables

Create a .env file in the root directory of your project with the following content:

- POSTGRES_USER=your_user_name
- POSTGRES_PASSWORD=your_password
- POSTGRES_DATABASE=your_database_name
- POSTGRES_LOCAL_PORT=your_local_port
- POSTGRES_DOCKER_PORT=your_docker_port

- SPRING_LOCAL_PORT=your_local_port
- SPRING_DOCKER_PORT=your_docker_port
- DEBUG_PORT=your_debug_port 

- TELEGRAM_BOT_USERNAME=your_telegram_bot
- TELEGRAM_BOT_TOKEN=your_bot_token
- TELEGRAM_GROUP_ID=your_group_id

- JWT_EXPIRATION=your_expiration_time
- JWT_SECRET=your_jwt_secret

- STRIPE_SECRET_KEY=your_stripe_secret

### Building and Running the Project ⚙️

1. **Clone the repository**:
    ```bash
    git clone https://github.com/jv-feb24-group-project5/booking-app.git
    cd booking-app
    ```

2. **Build the project**:
    ```bash
    mvn clean package
    ```

3. **Build and start Docker containers**:
    ```bash
    docker-compose down --rmi all
   ```
   You can use this command for remove all unnecessary images
   ```bash
   docker-compose up --build   
    ```
   This command for build new images and run app in docker
### Accessing the Application

- **Web Application**: Open your browser and go to `http://localhost:8083` to access the web application.
- **Swagger UI**: The API documentation is available at `http://localhost:8083/swagger-ui/index.html`.
### Authentication

The application uses JWT for secured access. Use the following credentials to log in as an admin:

    Login: admin@example.com
    Password: StrongPassword123

### Testing the Setup 👀

After starting the application, you can verify the setup by accessing the Swagger UI link.
Ensure that the web application is running and the APIs are accessible.

## Postman Collection 📨
To facilitate API testing, a Postman collection is provided.
You can import this collection into Postman to test the various endpoints.

    Postman Collection Link: https://elements.getpostman.com/redirect?entityId=34471690-e9da62fc-fc5a-46f4-9da4-d39542a62132&entityType=collection

The collection includes pre-configured requests for all available endpoints,
making it easier to test the application's functionalities.

## Conclusion 🏁

This Accommodation Service Web Application provides a comprehensive solution for users to manage
their bookings and for administrators to manage accommodations and user access.
The project leverages robust technologies and tools to ensure a secure and scalable platform.
We welcome your feedback and contributions to further enhance this application.

### Authors 🧑‍🧑‍🧒‍🧒🧑‍🧑‍
- [Anton Haiduk](https://github.com/TonyH277)
- [Oleksandr Molchanov](https://github.com/MolchanovAlexander)
- [Viktor Kushnir](https://github.com/vikkushnir)
- [Oleksandr Samoylenko](https://github.com/slizko1)
- [Vladyslav Radko](https://github.com/VolandevlodD)
- [Pavlo Betsa](https://github.com/Nikname2303)
