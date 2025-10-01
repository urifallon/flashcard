package com.flashcard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"studySessionItems"})
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class StudySessionRun {

//    -- STUDY_SESSION_RUNS
//    CREATE TABLE study_session_runs (
//            id BIGSERIAL PRIMARY KEY,
//            user_id BIGINT REFERENCES users(id),
//    folder_id BIGINT REFERENCES folders(id),
//    start_time TIMESTAMP DEFAULT now(),
//    end_time TIMESTAMP,
//    mode VARCHAR(20)
//);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime start_time;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime end_time;

    @Column(nullable = false)
    private String mode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @OneToMany(mappedBy = "studySessionRun")
    private List<StudySessionItem> studySessionItems = new ArrayList<>();
}
