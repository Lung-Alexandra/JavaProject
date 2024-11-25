package com.proiect.appointment_booking_system.request;

//import jakarta.validation.constraints.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.processing.Pattern;

public class UserRequest {

//    @NotNull(message = "Name is mandatory")
//    @NotBlank(message = "Name cannot be blank")
    private String name;

//    @NotNull(message = "Email is mandatory")
//    @Email(message = "Email should be valid")
    private String email;

//    @NotNull(message = "Password is mandatory")
//    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;

//    @NotNull(message = "Phone number is mandatory")
//    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

//    @NotNull(message = "Role is mandatory")
    private String role; // Could be validated further to match enum values

    public UserRequest(String name, String email, String password, String phoneNumber, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRole() {
        return role;
    }
}
