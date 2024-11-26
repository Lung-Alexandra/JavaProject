#  Online Appointment Booking System for Medical Clinics

The project is an online appointment booking system for medical clinics, 
built using Java Spring Boot. It will allow patients to schedule appointments 
with healthcare providers, view available time slots, and receive reminders 
while offering clinics tools for managing appointments efficiently.

###  10 Business Requirements




### Schema bazei de date
Diagram containing the 6 entities and the relationships between them. 
Relationships include One-to-One, One-to-Many, and Many-to-One.


![diagrama](diagram.svg)

Entities and Relationships:
1. `User`
    - Generalizes all users (patients, doctors, administrators).
    - Has One-to-One relationships with Patient and Doctor.
   
2. `Doctor`
   - Extends users to include doctors.
   - Many-to-One relationship with Clinic.
   
3. `Patient`
    - Extends users to include patients.
    - One-to-Many relationship with Appointment.
   
4. `Clinic`
   - Represents clinics where doctors work.
   - One-to-Many relationship with Doctor.
   
5. `Appointment`
   - Links patients, doctors, and clinics.
   - Relationships:
     - Many-to-One with Doctor.
     - Many-to-One with Patient.
     - Many-to-One with Clinic.
     
6. `Notification`
   - Represents messages sent to users about appointments.
   - Relationships:
     - Many-to-One with User.
     - Many-to-One with Appointment.


* User (shared by both Doctor and Patient)
* Doctor (extends User)
* Patient (extends User)
* Clinic
* Appointment
* Notification



