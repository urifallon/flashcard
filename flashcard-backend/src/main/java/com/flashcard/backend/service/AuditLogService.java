package com.flashcard.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashcard.backend.constants.AuditAction;
import com.flashcard.backend.dto.action.UserLogDto;
import com.flashcard.backend.model.AuditLog;
import com.flashcard.backend.model.User;
import com.flashcard.backend.repository.AuditLogRepository;
import com.flashcard.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public void logLogin(UserDetails userDetails) {
        User oldUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + userDetails.getUsername())
        );

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(oldUser);
        auditLog.setAction(AuditAction.LOGIN.toString());

        String userDetailsJson;
        try {
            UserLogDto logDto = new UserLogDto(
                    oldUser.getUsername(),
                    oldUser.getEmail(),
                    oldUser.getRole(),
                    oldUser.getCreated_at(),
                    oldUser.getUpdated_at()
            );
            userDetailsJson = objectMapper.writeValueAsString(logDto);
        } catch (JsonProcessingException e) {
            System.err.println("ERROR serializing UserDetails for audit log: " + e.getMessage());
            userDetailsJson = "Serialization_Error: Could not convert UserDetails to JSON.";
        }

        auditLog.setMetadata(userDetailsJson);
        auditLogRepository.save(auditLog);
    }
}
