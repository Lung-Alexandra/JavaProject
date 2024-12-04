package com.proiect.appointment_booking_system.service;

import aj.org.objectweb.asm.commons.Remapper;
import com.proiect.appointment_booking_system.dto.UserDTO;
import com.proiect.appointment_booking_system.exceptions.UserAlreadyExists;
import com.proiect.appointment_booking_system.mapper.UserMapper;
import com.proiect.appointment_booking_system.model.User;
import com.proiect.appointment_booking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public void registerUser(UserDTO userDTO) {
        // Verificăm dacă utilizatorul există deja pe baza email-ului sau numărului de telefon
        if (userRepository.existsByEmail(userDTO.getEmail())|| userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new UserAlreadyExists();
        }

        // Transformăm UserDTO în User (entitate)
        User user = UserMapper.toEntity(userDTO);

        // Salvăm utilizatorul în baza de date
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
    
    public void updateUser(UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        userRepository.save(user);
    }


    public List<UserDTO> getAllUsers() {
        // Obține lista de utilizatori din baza de date
        List<User> users = userRepository.findAll();

        // Transformă lista de entități User în lista de DTO-uri UserDTO
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

}
