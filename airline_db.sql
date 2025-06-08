
CREATE DATABASE IF NOT EXISTS airline_db;

USE airline_db;


-- USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('Admin', 'Operator', 'Customer') NOT NULL,
    status BOOLEAN DEFAULT TRUE
);

INSERT INTO users (username, password, role, status) 
VALUES ('admin', 'admin', 'Admin', TRUE);

-- AIRPORTS TABLE
CREATE TABLE IF NOT EXISTS airports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL
);

-- AIRPLANES TABLE
CREATE TABLE IF NOT EXISTS airplanes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    type ENUM('small', 'medium', 'large') NOT NULL,
    current_airport_id INT,
    is_available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (current_airport_id) REFERENCES airports(id)
);

-- SEATS TABLE
CREATE TABLE IF NOT EXISTS seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    airplane_id INT NOT NULL,
    total_seats INT NOT NULL,

    first_class INT NOT NULL,
    business_class INT NOT NULL,
    economy_class INT NOT NULL,

    available_first_class INT NOT NULL DEFAULT 0,
    available_business_class INT NOT NULL DEFAULT 0,
    available_economy_class INT NOT NULL DEFAULT 0,

    FOREIGN KEY (airplane_id) REFERENCES airplanes(id) ON DELETE CASCADE
);

-- TRIGGER: Set available seats on insert
DELIMITER $$

CREATE TRIGGER set_available_seats_before_insert
BEFORE INSERT ON seats
FOR EACH ROW
BEGIN
    SET NEW.available_first_class = IF(NEW.first_class > 0, NEW.first_class, 0);
    SET NEW.available_business_class = IF(NEW.business_class > 0, NEW.business_class, 0);
    SET NEW.available_economy_class = IF(NEW.economy_class > 0, NEW.economy_class, 0);
END$$

DELIMITER ;

-- FLIGHTS TABLE
CREATE TABLE IF NOT EXISTS flights (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(10) NOT NULL,
    airplane_id INT NOT NULL,
    departure_airport_id INT NOT NULL,
    arrival_airport_id INT NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    status VARCHAR(20) DEFAULT 'Scheduled',

    FOREIGN KEY (airplane_id) REFERENCES airplanes(id),
    FOREIGN KEY (departure_airport_id) REFERENCES airports(id),
    FOREIGN KEY (arrival_airport_id) REFERENCES airports(id)
);

-- BOOKINGS TABLE
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    flight_id INT NOT NULL,
    class ENUM('First', 'Business', 'Economy') NOT NULL,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (flight_id) REFERENCES flights(id)
);

-- TRIGGER: Update available seats on seats update
DELIMITER $$

CREATE TRIGGER update_available_seats_before_update
BEFORE UPDATE ON seats
FOR EACH ROW
BEGIN
    IF NEW.first_class <> OLD.first_class THEN
        SET NEW.available_first_class = IF(NEW.first_class > 0, NEW.first_class, 0);
    END IF;

    IF NEW.business_class <> OLD.business_class THEN
        SET NEW.available_business_class = IF(NEW.business_class > 0, NEW.business_class, 0);
    END IF;

    IF NEW.economy_class <> OLD.economy_class THEN
        SET NEW.available_economy_class = IF(NEW.economy_class > 0, NEW.economy_class, 0);
    END IF;
END$$

DELIMITER ;
