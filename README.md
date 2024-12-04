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






