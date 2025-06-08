CSC 506 1.0 Computer Programming Laboratory  
Final Assignment – 2025  

Name with Initials: E.M.K.G.A.D.B.Ekanayake  
Index Number: GSCOMP265 
Project Title: Airline Ticket Booking System  

Project Structure:
------------------

1. Source Code:
   - Located in the folder: `AirlineBookingSystem/`
   - Developed as a NetBeans project.
   - Organized using MVC pattern:
     • `controllers/` – Business logic controllers
     • `dao/` – Data access classes
     • `db/` – Database connection handler
     • `models/` – Domain objects (e.g., Flight, Booking, User)
     • `airline/gui/` – Java Swing UI components

2. Executable:
   - Runnable JAR file: `AirlineBookingSystem.jar` (located in `dist/`)
   - Requires MySQL Connector/J (located in `libs/`)

3. Sample Database:
   - SQL Backup: `airline_db.sql`
   - Includes preloaded data for users


How to Run the System:
----------------------

1. **Database Setup:**
   - Open MySQL Workbench or any SQL client.
   - Import the `airline_db.sql` file to create and populate the database.

2. **Run the JAR:**
   Open terminal or PowerShell and navigate to the project root directory, then run:




(Make sure Java 17 is installed.)

Login Credentials:
------------------

**Administrator**
- Username: admin
- Password: Admin

Note:
-----
- Ensure MySQL service is running before launching the application.
- Any ClassNotFound or driver issues can usually be resolved by verifying the JDBC driver is correctly referenced in the classpath.

Author:
-------
Name: A.B.C. Perera  
Index No: GSCOMP123  
Submission Date: 31st May 2025
