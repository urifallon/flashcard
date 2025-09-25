package com.flashcard.backend.controller;

import com.flashcard.backend.dto.request.LoginDto;
import com.flashcard.backend.dto.request.RegisterDto;
import com.flashcard.backend.dto.response.LoginResponseDto;
import com.flashcard.backend.model.User;
import com.flashcard.backend.service.JwtService;
import com.flashcard.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

//    @Autowired
//    public AuthController(UserService userService) {
//        this.userService = userService;
//    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        try {
            User newUser = userService.registerUser(registerDto);
            newUser.setPassword(null);
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

        return ResponseEntity.ok(loginResponse);
    }
}

