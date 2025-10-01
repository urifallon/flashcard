package com.flashcard.backend.controller;

import com.flashcard.backend.dto.action.UserLogDto;
import com.flashcard.backend.dto.request.LoginDto;
import com.flashcard.backend.dto.request.RegisterDto;
import com.flashcard.backend.dto.response.LoginResponseDto;
import com.flashcard.backend.model.User;
import com.flashcard.backend.repository.UserRepository;
import com.flashcard.backend.service.AuditLogService;
import com.flashcard.backend.service.JwtService;
import com.flashcard.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        try {
            User newUser = userService.registerUser(registerDto);
            newUser.setPassword_hash(null);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@RequestBody LoginDto loginDto) {
        // Xác thực người dùng bằng email và mật khẩu
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        // Lấy thông tin người dùng đã được xác thực
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Tạo JWT token
        String jwtToken = jwtService.generateToken(userDetails);

        // Tạo đối tượng trả về
        LoginResponseDto loginResponse = LoginResponseDto.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime()) // Cần thêm hàm này vào JwtService
                .build();

        auditLogService.logLogin(userDetails);

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserLogDto> me() {
        log.info("Entering /me endpoint");
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("Authentication object: {}", authentication);

            User userPrincipal = (User) authentication.getPrincipal();
            log.info("Principal object: {}", userPrincipal);

            User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
                    () -> new UsernameNotFoundException("User not found with email: " + userPrincipal.getEmail())
            );
            log.info("User fetched from repository: {}", user);

            UserLogDto userLogDto = UserLogDto.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .created_at(user.getCreated_at())
                    .updated_at(user.getUpdated_at())
                    .build();
            log.info("UserLogDto created: {}", userLogDto);

            log.info("Attempting to return response entity with DTO...");
            ResponseEntity<UserLogDto> response = ResponseEntity.ok(userLogDto);
            log.info("Response entity created successfully. Body: {}", response.getBody());
            return response;
        } catch (Exception e) {
            log.error("!!! EXCEPTION in /me endpoint !!!", e);
            // Return an error response so the client knows something went wrong
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

