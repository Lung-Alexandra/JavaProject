# CREATE TABLE IF NOT EXISTS `User`
# (
#     `id`           INT AUTO_INCREMENT PRIMARY KEY,
#     `name`         VARCHAR(255),
#     `email`        VARCHAR(255),
#     `password`     VARCHAR(255),
#     `role`         ENUM ('PATIENT', 'DOCTOR', 'ADMIN'),
#     `phone_number` VARCHAR(20),
#     `created_at`   DATETIME DEFAULT CURRENT_TIMESTAMP,
#     `updated_at`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
# );
#
# CREATE TABLE IF NOT EXISTS `Clinic`
# (
#     `id`             INT AUTO_INCREMENT PRIMARY KEY,
#     `name`           VARCHAR(255),
#     `location`       VARCHAR(255),
#     `contact_number` VARCHAR(20),
#     `email`          VARCHAR(255)
# );
#
# CREATE TABLE IF NOT EXISTS `Doctor`
# (
#     `id`                    INT AUTO_INCREMENT PRIMARY KEY,
#     `user_id`               INT,
#     `specialization`        VARCHAR(255),
#     `clinic_id`             INT,
#     `availability_schedule` TEXT,
#     FOREIGN KEY (`user_id`) REFERENCES `User` (`id`),
#     FOREIGN KEY (`clinic_id`) REFERENCES `Clinic` (`id`)
# );
#
# CREATE TABLE IF NOT EXISTS `Patient`
# (
#     `id`              INT AUTO_INCREMENT PRIMARY KEY,
#     `user_id`         INT,
#     `medical_history` TEXT,
#     `address`         VARCHAR(255),
#     FOREIGN KEY (`user_id`) REFERENCES `User` (`id`)
# );
#
#
#
# CREATE TABLE IF NOT EXISTS `Appointment`
# (
#     `id`               INT AUTO_INCREMENT PRIMARY KEY,
#     `patient_id`       INT,
#     `doctor_id`        INT,
#     `clinic_id`        INT,
#     `appointment_date` DATE,
#     `appointment_time` TIME,
#     `status`           ENUM ('BOOKED', 'CANCELLED', 'COMPLETED'),
#     FOREIGN KEY (`patient_id`) REFERENCES `Patient` (`id`),
#     FOREIGN KEY (`doctor_id`) REFERENCES `Doctor` (`id`),
#     FOREIGN KEY (`clinic_id`) REFERENCES `Clinic` (`id`)
# );
#
# CREATE TABLE IF NOT EXISTS `Notification`
# (
#     `id`                INT AUTO_INCREMENT PRIMARY KEY,
#     `user_id`           INT,
#     `appointment_id`    INT,
#     `notification_type` ENUM ('REMINDER', 'CANCELLATION'),
#     `sent_at`           DATETIME DEFAULT CURRENT_TIMESTAMP,
#     FOREIGN KEY (`user_id`) REFERENCES `User` (`id`),
#     FOREIGN KEY (`appointment_id`) REFERENCES `Appointment` (`id`)
# );

#
# INSERT INTO users (name, email, password, role, phone_number, created_at, updated_at)
# VALUES
#     ('John Doe', 'john.doe@example.com', 'password123', 'PATIENT', '1234567890', NOW(), NOW()),
#     ('Alice Smith', 'alice.smith@example.com', 'password123', 'DOCTOR', '0987654321', NOW(), NOW()),
#     ('Admin User', 'admin@example.com', 'adminpass', 'ADMIN', '1122334455', NOW(), NOW());
#
# INSERT INTO clinics (name, location, contact_number, email)
# VALUES
#     ('City Health Clinic', '123 Main St, Springfield', '1234567890', 'info@cityhealth.com'),
#     ('Downtown Medical Center', '456 Elm St, Springfield', '0987654321', 'contact@downtownmed.com');
#
#
# INSERT INTO doctors (user_id, specialization, clinic_id, availability_schedule)
# VALUES
#     (2, 'Cardiologist', 1, '{"Monday": "9:00-17:00", "Wednesday": "9:00-17:00"}');
#
#
# INSERT INTO patients (user_id, medical_history, address)
# VALUES
#     (1, '{"allergies": "None", "chronic_conditions": "None"}', '789 Oak St, Springfield');
#


SHOW TABLES;

select * from users;
select * from doctors;
select * from doctor_clinic;
select * from patients;
select * from clinics;
select * from appointments;
select * from notifications;
DELETE FROM appointments
WHERE id = 2;
DELETE FROM Notifications
WHERE appointment_id = 2;
# DROP TABLE IF EXISTS notifications;
# DROP TABLE IF EXISTS appointments;
# DROP TABLE IF EXISTS doctor_clinic;
# DROP TABLE IF EXISTS doctors;
# DROP TABLE IF EXISTS patients;
# DROP TABLE IF EXISTS clinics;
# DROP TABLE IF EXISTS users;
