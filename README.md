# Clinic360 - Healthcare Management System

Clinic360 is a comprehensive healthcare management system designed to streamline the operations of medical clinics and improve patient care. The system provides separate interfaces for patients, doctors, and administrators to manage appointments and clinic operations efficiently.

## Features

### Patient Portal
- User registration and authentication
- Profile management
- Appointment booking and management
- Secure password management

### Doctor Portal
- Patient management
- Appointment scheduling
- Patient consultation notes

### Admin Portal
- Doctor Registration
- Doctor management
- Appointment oversight

## Technical Stack

- **Backend**: Spring Boot 3.x
- **Frontend**: 
  - Thymeleaf
  - Bootstrap 5
  - Bootstrap Icons
- **Database**: MySQL
- **Security**: Spring Security
- **ORM**: Spring Data JPA/Hibernate

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Database Configuration

The application uses MySQL as its database. Configure the following properties in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/clinic360?createDatabaseIfNotExist=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/clinic360.git
```

2. Navigate to the project directory:
```bash
cd clinic360
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── clinic360/
│   │           ├── config/
│   │           ├── controller/
│   │           ├── model/
│   │           ├── repository/
│   │           ├── service/
│   │           ├── Clinic360Application.java
│   │           └── validator/
│   │               ├── Email.java
│   │               └── Length.java
│   └── resources/
│       ├── static/
│       │   └── css/
│       ├── templates/
│       │   ├── patient/
│       │   ├── doctor/
│       │   └── admin/
│       │       └── doctors.html
│       └── application.properties
```
## Security Features

- Role-based access control (ROLE_PATIENT, ROLE_DOCTOR, ROLE_ADMIN)
- Password encryption using BCrypt
- CSRF protection
- Session management
- Secure password requirements:
  - Minimum 8 characters
  - At least one letter
  - At least one number
  - Special characters allowed

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, email support@clinic360.com or create an issue in the repository.

## Acknowledgments

- Bootstrap for the UI components
- Spring Boot team for the excellent framework
- All contributors who have helped shape this project
