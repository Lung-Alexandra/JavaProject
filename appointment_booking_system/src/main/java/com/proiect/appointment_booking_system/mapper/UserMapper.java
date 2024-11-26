package com.proiect.appointment_booking_system.mapper;


import com.proiect.appointment_booking_system.enums.Role;
import com.proiect.appointment_booking_system.model.User;
import com.proiect.appointment_booking_system.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole().toString()); // Enum to String
        userDTO.setPhoneNumber(user.getPhoneNumber());
        return userDTO;
    }

    public static User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(Role.valueOf(String.valueOf(Role.valueOf(userDTO.getRole())))); // String to Enum
        user.setPhoneNumber(userDTO.getPhoneNumber());
        return user;
    }
}
