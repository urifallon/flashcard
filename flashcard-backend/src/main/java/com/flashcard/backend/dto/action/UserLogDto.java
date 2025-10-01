package com.flashcard.backend.dto.action;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Builder
@Jacksonized
public record UserLogDto(String username, String email, String role, LocalDateTime created_at,
                         LocalDateTime updated_at) {
}
