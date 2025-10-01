package com.flashcard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user", "card"})
@Builder
@Table(name = "user_flashcard_stats")
public class UserCardStats {

    @EmbeddedId
    private UserCardStatsId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("flashcardId")
    @JoinColumn(name = "flashcard_id")
    private Card card;

    @Column(nullable = false)
    private int times_studied = 0;

    @Column(nullable = false)
    private int times_correct = 0;

    @Column(nullable = false)
    private int times_wrong = 0;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime last_studied;

    @Column(nullable = false)
    private int score;


}
