# AI-Enhanced-Event-Management-System
AI Enhanced event management system with AI features like Chatbot, Attendance prediction and conflict prediction
# AI-Enhanced Event Management System

## Overview
A comprehensive event management application built with Java that leverages AI capabilities to streamline event creation, registration, and management. The system provides an intuitive interface for organizing events and managing attendee registrations.

## Features
- **Event Creation & Management**: Create, update, and manage events with detailed information
- **User Registration**: Allow users to register for events seamlessly
- **AI-Enhanced Functionality**: Smart recommendations and intelligent event management
- **Theme Customization**: Personalize the application with theme management
- **DAO Pattern**: Efficient data access layer for database operations
- **User Authentication**: Secure user login and profile management

## Project Structure
```
├── src/
│   ├── Registration.class         # Registration entity
│   ├── RegistrationDAO.class      # Registration data access
│   ├── Theme.class                # Theme entity
│   ├── ThemeManager.class         # Theme management
│   ├── User.class                 # User entity
│   ├── UserDAO.class              # User data access
│   └── ViewRegistrationsDialog.class  # UI dialog component
├── target/                         # Compiled classes
├── pom.xml                         # Maven dependencies (if applicable)
└── README.md                       # This file
```

## Technologies Used
- **Language**: Java
- **Architecture**: DAO (Data Access Object) Pattern
- **Database**: [Specify your database - e.g., MySQL, SQLite, PostgreSQL]
- **Build Tool**: Maven/Gradle (if applicable)

## Getting Started

### Prerequisites
- Java JDK 8 or higher
- [Database software if required]
- IDE: IntelliJ IDEA, Eclipse, or similar

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/jyoshitaa9806-ctrl/AI-Enhanced-Event-Management-System.git
   ```

2. Navigate to the project directory:
   ```bash
   cd AI-Enhanced-Event-Management-System
   ```

3. Compile the Java files:
   ```bash
   javac -d target src/*.java
   ```

4. Run the application:
   ```bash
   java -cp target Main
   ```

## Usage
1. Launch the application by running Main.java
2. Login with your credentials or register a new account
3. Create events from the organizer dashboard
4. Generate QR codes for event tickets
5. Manage registrations and view attendee lists
6. Customize themes from the settings menu

## Core Classes

### Registration
Manages event registration data and operations.

### User
Represents user entities with authentication and profile management.

### Theme
Handles theme-related configurations and customizations.

### DAO Classes
- `RegistrationDAO`: Handles registration data persistence
- `UserDAO`: Manages user data operations
ViewRegistrationsDialog
Provides UI components to view and manage event registrations.

Future Enhancements
- Integration with calendar services
- Email notifications for event updates
- Advanced analytics and reporting
- Mobile application support
- Enhanced AI recommendations

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## License
[Specify your license - e.g., MIT, Apache 2.0]

## Author
jg3044-hash

## Support
For issues or questions, please open an issue on the GitHub repository.

---
**Last Updated**: October 2025
