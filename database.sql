-- =======================================================
-- SMART PARKING MANAGEMENT SYSTEM - DATABASE SCHEMA
-- Database: smart_parking_db
-- Compatibility: MySQL 8.0+
-- =======================================================

CREATE DATABASE IF NOT EXISTS smart_parking_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE smart_parking_db;

-- -------------------------------------------------------
-- 1. Table: admins
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL DEFAULT NULL,
    INDEX idx_admin_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- 2. Table: vehicles
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
    registration_number VARCHAR(20) NOT NULL UNIQUE,
    vehicle_type ENUM('BIKE', 'CAR', 'TRUCK') NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_reg_number (registration_number),
    INDEX idx_owner_phone (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- 3. Table: parking_slots
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS parking_slots (
    slot_id INT AUTO_INCREMENT PRIMARY KEY,
    slot_number VARCHAR(20) NOT NULL UNIQUE,
    floor_number INT NOT NULL DEFAULT 1,
    slot_type ENUM('BIKE', 'CAR', 'TRUCK', 'GENERAL') NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    current_vehicle_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_slot_vehicle FOREIGN KEY (current_vehicle_id) 
        REFERENCES vehicles(vehicle_id) ON DELETE SET NULL,
    INDEX idx_slot_status (status),
    INDEX idx_slot_type (slot_type),
    INDEX idx_slot_floor (floor_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- 4. Table: parking_records
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS parking_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id INT NULL,
    vehicle_number VARCHAR(20) NOT NULL,
    vehicle_type ENUM('BIKE', 'CAR', 'TRUCK') NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    slot_id INT NOT NULL,
    slot_number VARCHAR(20) NOT NULL,
    entry_time DATETIME NOT NULL,
    exit_time DATETIME NULL,
    duration_hours INT NULL,
    total_fee DECIMAL(10, 2) NULL,
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_record_vehicle FOREIGN KEY (vehicle_id) 
        REFERENCES vehicles(vehicle_id) ON DELETE SET NULL,
    CONSTRAINT fk_record_slot FOREIGN KEY (slot_id) 
        REFERENCES parking_slots(slot_id),
    INDEX idx_record_vehicle_num (vehicle_number),
    INDEX idx_record_status (status),
    INDEX idx_record_entry_time (entry_time),
    INDEX idx_record_exit_time (exit_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- 5. Table: payments
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'UPI') NOT NULL DEFAULT 'CASH',
    payment_time DATETIME NOT NULL,
    transaction_reference VARCHAR(100) NOT NULL UNIQUE,
    status ENUM('SUCCESS', 'PENDING', 'FAILED') NOT NULL DEFAULT 'SUCCESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_record FOREIGN KEY (record_id) 
        REFERENCES parking_records(record_id) ON DELETE CASCADE,
    INDEX idx_payment_time (payment_time),
    INDEX idx_payment_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- SEED DATA: Default Admin
-- Username: admin
-- Password: admin123
-- -------------------------------------------------------
INSERT INTO admins (username, password_hash, salt, full_name, email, role)
VALUES (
    'admin',
    'cf8eaef3ff404d96b133e4f757749eef71a512255619068b1c27858f5ebe2026',
    'e3b0c44298fc1c14',
    'System Administrator',
    'admin@smartparking.com',
    'SUPER_ADMIN'
)
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);

-- -------------------------------------------------------
-- SEED DATA: Sample Registered Vehicles
-- -------------------------------------------------------
INSERT INTO vehicles (registration_number, vehicle_type, owner_name, phone_number) VALUES
('KA-01-AB-1234', 'BIKE', 'Rahul Sharma', '9876543210'),
('DL-03-XY-9876', 'CAR', 'Priya Verma', '9812345678'),
('MH-12-CD-5544', 'CAR', 'Amit Patel', '9823456789'),
('HR-26-EE-4321', 'TRUCK', 'Vikram Singh', '9834567890'),
('TS-09-FA-7788', 'CAR', 'Sneha Reddy', '9845678901'),
('UP-32-ZZ-1122', 'BIKE', 'Mohit Gupta', '9856789012')
ON DUPLICATE KEY UPDATE owner_name = VALUES(owner_name);

-- -------------------------------------------------------
-- SEED DATA: Parking Slots (Floor 1 & Floor 2)
-- -------------------------------------------------------
-- Floor 1: Bike Slots (A-101 to A-110)
INSERT INTO parking_slots (slot_number, floor_number, slot_type, status) VALUES
('A-101', 1, 'BIKE', 'AVAILABLE'),
('A-102', 1, 'BIKE', 'AVAILABLE'),
('A-103', 1, 'BIKE', 'AVAILABLE'),
('A-104', 1, 'BIKE', 'AVAILABLE'),
('A-105', 1, 'BIKE', 'AVAILABLE'),
('A-106', 1, 'BIKE', 'AVAILABLE'),
('A-107', 1, 'BIKE', 'AVAILABLE'),
('A-108', 1, 'BIKE', 'AVAILABLE'),
('A-109', 1, 'BIKE', 'AVAILABLE'),
('A-110', 1, 'BIKE', 'AVAILABLE')
ON DUPLICATE KEY UPDATE slot_type = VALUES(slot_type);

-- Floor 1: Car Slots (A-111 to A-125)
INSERT INTO parking_slots (slot_number, floor_number, slot_type, status) VALUES
('A-111', 1, 'CAR', 'AVAILABLE'),
('A-112', 1, 'CAR', 'AVAILABLE'),
('A-113', 1, 'CAR', 'AVAILABLE'),
('A-114', 1, 'CAR', 'AVAILABLE'),
('A-115', 1, 'CAR', 'AVAILABLE'),
('A-116', 1, 'CAR', 'AVAILABLE'),
('A-117', 1, 'CAR', 'AVAILABLE'),
('A-118', 1, 'CAR', 'AVAILABLE'),
('A-119', 1, 'CAR', 'AVAILABLE'),
('A-120', 1, 'CAR', 'AVAILABLE'),
('A-121', 1, 'CAR', 'AVAILABLE'),
('A-122', 1, 'CAR', 'AVAILABLE'),
('A-123', 1, 'CAR', 'AVAILABLE'),
('A-124', 1, 'CAR', 'AVAILABLE'),
('A-125', 1, 'CAR', 'AVAILABLE')
ON DUPLICATE KEY UPDATE slot_type = VALUES(slot_type);

-- Floor 2: Car Slots (B-201 to B-210)
INSERT INTO parking_slots (slot_number, floor_number, slot_type, status) VALUES
('B-201', 2, 'CAR', 'AVAILABLE'),
('B-202', 2, 'CAR', 'AVAILABLE'),
('B-203', 2, 'CAR', 'AVAILABLE'),
('B-204', 2, 'CAR', 'AVAILABLE'),
('B-205', 2, 'CAR', 'AVAILABLE'),
('B-206', 2, 'CAR', 'AVAILABLE'),
('B-207', 2, 'CAR', 'AVAILABLE'),
('B-208', 2, 'CAR', 'AVAILABLE'),
('B-209', 2, 'CAR', 'AVAILABLE'),
('B-210', 2, 'CAR', 'AVAILABLE')
ON DUPLICATE KEY UPDATE slot_type = VALUES(slot_type);

-- Floor 2: Truck Slots (B-211 to B-220)
INSERT INTO parking_slots (slot_number, floor_number, slot_type, status) VALUES
('B-211', 2, 'TRUCK', 'AVAILABLE'),
('B-212', 2, 'TRUCK', 'AVAILABLE'),
('B-213', 2, 'TRUCK', 'AVAILABLE'),
('B-214', 2, 'TRUCK', 'AVAILABLE'),
('B-215', 2, 'TRUCK', 'AVAILABLE'),
('B-216', 2, 'TRUCK', 'AVAILABLE'),
('B-217', 2, 'TRUCK', 'AVAILABLE'),
('B-218', 2, 'TRUCK', 'AVAILABLE'),
('B-219', 2, 'TRUCK', 'AVAILABLE'),
('B-220', 2, 'TRUCK', 'AVAILABLE')
ON DUPLICATE KEY UPDATE slot_type = VALUES(slot_type);

-- -------------------------------------------------------
-- SEED DATA: Historical Completed Parking & Payments
-- -------------------------------------------------------
INSERT INTO parking_records (
    record_id, vehicle_id, vehicle_number, vehicle_type, owner_name, phone_number,
    slot_id, slot_number, entry_time, exit_time, duration_hours, total_fee, status
) VALUES 
(1, 1, 'KA-01-AB-1234', 'BIKE', 'Rahul Sharma', '9876543210', 1, 'A-101', 
 DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR), 2, 80.00, 'COMPLETED'),
(2, 2, 'DL-03-XY-9876', 'CAR', 'Priya Verma', '9812345678', 11, 'A-111', 
 DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), 3, 300.00, 'COMPLETED'),
(3, 4, 'HR-26-EE-4321', 'TRUCK', 'Vikram Singh', '9834567890', 36, 'B-211', 
 DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR), 4, 600.00, 'COMPLETED')
ON DUPLICATE KEY UPDATE vehicle_number = VALUES(vehicle_number);

INSERT INTO payments (
    payment_id, record_id, amount, payment_method, payment_time, transaction_reference, status
) VALUES
(1, 1, 80.00, 'UPI', DATE_SUB(NOW(), INTERVAL 3 HOUR), 'TXN-SMP-1001-UPI', 'SUCCESS'),
(2, 2, 300.00, 'CASH', DATE_SUB(NOW(), INTERVAL 1 HOUR), 'TXN-SMP-1002-CSH', 'SUCCESS'),
(3, 3, 600.00, 'CARD', DATE_SUB(NOW(), INTERVAL 4 HOUR), 'TXN-SMP-1003-CRD', 'SUCCESS')
ON DUPLICATE KEY UPDATE amount = VALUES(amount);
