package com.flashcard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudySessionItem {
//    CREATE TABLE study_session_items (
//            id BIGSERIAL PRIMARY KEY,
//            session_run_id BIGINT REFERENCES study_session_runs(id),
//    flashcard_id BIGINT REFERENCES flashcards(id),
//    result BOOLEAN,
//    score_delta INT,
//    studied_at TIMESTAMP DEFAULT now()
//);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean result;

    @Column(nullable = false)
    private int score_delta;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime studied_at;

    @ManyToOne
    @JoinColumn(name = "session_run_id", nullable = false)
    private StudySessionRun studySessionRun;

    @ManyToOne
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Card card;

}
