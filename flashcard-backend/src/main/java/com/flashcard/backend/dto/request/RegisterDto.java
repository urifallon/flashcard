package com.flashcard.backend.dto.request;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String email;
    private String password;
}
