package com.proiect.appointment_booking_system.service;

import com.proiect.appointment_booking_system.dto.UserDTO;
import com.proiect.appointment_booking_system.exceptions.UserAlreadyExists;
import com.proiect.appointment_booking_system.mapper.UserMapper;
import com.proiect.appointment_booking_system.model.User;
import com.proiect.appointment_booking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail()) || userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new UserAlreadyExists();
        }

        User user = UserMapper.toEntity(userDTO);
        user.setPassword(encodePassword(user.getPassword()));
        userRepository.save(user);
    }

    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(UserMapper::toDTO);
    }

    public Optional<UserDTO> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).map(UserMapper::toDTO);
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(UserMapper::toDTO);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public void updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getName() != null) {
            existingUser.setName(userDTO.getName());
        }
        if (userDTO.getEmail() != null) {
            if (userRepository.existsByEmail(userDTO.getEmail()) && !existingUser.getEmail().equals(userDTO.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            existingUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getPassword() != null) {
            existingUser.setPassword(encodePassword(userDTO.getPassword()));
        }

        userRepository.save(existingUser);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    private String encodePassword(String rawPassword) {
        if (passwordEncoder == null || rawPassword == null) {
            return rawPassword;
        }
        return passwordEncoder.encode(rawPassword);
    }
}
