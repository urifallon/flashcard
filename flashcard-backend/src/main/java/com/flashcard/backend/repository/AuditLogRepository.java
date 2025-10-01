package com.flashcard.backend.repository;

import com.flashcard.backend.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    public Optional<AuditLog> findById(Long id);
}
