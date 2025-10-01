package com.flashcard.backend.service;

import com.flashcard.backend.dto.request.RegisterDto;
import com.flashcard.backend.model.User;
import com.flashcard.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterDto registerDto) {
        userRepository.findByEmail(registerDto.getEmail()).ifPresent(u -> {
            throw new IllegalStateException("Email " + u.getEmail() + " đã được sử dụng.");
        });

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword_hash(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole("USER");

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        // Logic to update user
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        // etc. for other fields
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }



}
