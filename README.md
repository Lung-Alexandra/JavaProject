#  Online Appointment Booking System for Medical Clinics

The project is an online appointment booking system for medical clinics, 
built using Java Spring Boot. It will allow patients to schedule appointments 
with healthcare providers, view available time slots, and receive reminders 
while offering clinics tools for managing appointments efficiently.

###  10 Business Requirements




### Databese's Schema
Diagram containing the 6 entities and the relationships between them. 
The entities are:
* User (shared by both Doctor and Patient)
* Doctor (extends User)
* Patient (extends User)
* Clinic
* Appointment
* Notification

Relationships include One-to-One, One-to-Many, and Many-to-One and Many-to-Many.


![diagrama](diagram.svg)

Entities and Relationships:
1. `User`
    - Generalizes all users (patients, doctors).
    - Has One-to-One relationships with Patient and Doctor.
   
2. `Doctor`
   - Extends users to include doctors.
   - Many-to-Many relationship with Clinic.
   
3. `Patient`
    - Extends users to include patients.
    - One-to-Many relationship with Appointment.
    - One-to-Many relationship with Notification.
   
4. `Clinic`
   - Represents clinics where doctors work.
   - Many-to-Many relationship with Doctor.
   
5. `Appointment`
   - Links patients, doctors, and clinics.
   - Relationships:
     - Many-to-One with Doctor.
     - Many-to-One with Patient.
     - Many-to-One with Clinic.
     - One-to-One with Notification.
     
6. `Notification`
   - Represents messages sent to users about appointments.
   - Relationships:
     - Many-to-One with Patient.
     - One-to-One with Appointment.



1. REST Endpoints
   - Doctor
       1. `GET /doctors`
         - Returns a list of all doctors.
       2. `GET /doctors/{id}`
          - Returns a specific doctor by ID.
       3. `POST /doctors/reggister`
          - Registers a new doctor.
       4. `DELETE /doctors/{id}` 
          - Deletes a doctor by ID.
    - Clinic
       1. `GET /clinics`
          - Returns a list of all clinics.
       2. `GET /clinics/{id}`
          - Returns a specific clinic by ID.
       3. `GET /clinics/email` 
          - Returns a specific clinic by email. 
       4. `POST /clinics/register`
          - Registers a new clinic.
       5. `DELETE /clinics/{id}`
          - Deletes a clinic by ID.
       6. `GET /clinics/{clinicId}/doctors`
          - Returns a list of doctors working at a specific clinic.
       7. `DELETE /clinics/{id}`
          - Removes a clinic.



10 Business Requirements:

### 1. Patient, Doctor, Clinic Management


System must allow patients to register and maintain personal profiles
System must securely store patient medical history
System must track patient appointment history


System must support doctor registration with specializations
System must maintain doctor schedules and availability
Doctors must be able to work at multiple clinics


System must allow registration of multiple clinics
Each clinic must maintain its own roster of doctors
System must track clinic contact information and location


### 2.Appointment Scheduling


System must allow patients to book appointments with specific doctors
System must prevent double-booking of doctors
Appointments must include date, time, and status tracking


### 3.Notification System


System must send appointment confirmations
System must notify patients of upcoming appointments
System must alert relevant parties of appointment cancellations


### 4.User Authentication & Authorization


System must support different user roles (Patient, Doctor, Admin)
System must secure sensitive medical information
System must validate user credentials


### 5.Availability Management


System must track doctor availability across different clinics
System must handle scheduling conflicts
System must support different time slots for appointments


### 6.Medical Records


System must maintain patient medical history
System must ensure privacy of medical records
System must allow appropriate access to medical information


### 7.Search & Filter Functionality -- de facut 


System must allow searching for doctors by specialization
System must allow filtering clinics by location
System must support appointment search by date/time


### 8.Data Validation & Integrity


System must validate all input data
System must maintain referential integrity
System must prevent invalid appointment states

### 9.Doctor Transfer
System must allow doctors to be transfered from one clinic to another

### 10.Patient Appointments Tracking -- de facut
System must allow clinics to track the number of appointments of each patient in order to give them discounts



# 5 Main MVP Features:

### 1.User Management System


Registration and authentication for users (patients and doctors)
Basic profile management
Role-based access control
Implementation: UserController, UserService already implemented with basic CRUD operations


### 2.Clinic Management


Clinic registration and profile management
Doctor association with clinics
Basic clinic information management
Implementation: ClinicController, ClinicService provide required functionality


### 3.Appointment Scheduling


Basic appointment booking functionality
Date and time slot management
Appointment status tracking
Implementation: AppointmentController, AppointmentService handle core scheduling features


### 4.Doctor Management


Doctor ~~registration~~ with specializations
Multi-clinic association
Basic availability scheduling
Implementation: DoctorController, DoctorService manage doctor-related operations


### 5.Notification System


Basic appointment notifications
Simple notification types
Notification tracking
Implementation: NotificationController, NotificationService handle basic notifications




