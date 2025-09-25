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
        // Kiểm tra xem email đã tồn tại chưa
        userRepository.findByEmail(registerDto.getEmail()).ifPresent(u -> {
            throw new IllegalStateException("Email " + u.getEmail() + " đã được sử dụng.");
        });

        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole("USER"); // Gán vai trò mặc định

        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        // Logic to update user
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        // etc. for other fields
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
