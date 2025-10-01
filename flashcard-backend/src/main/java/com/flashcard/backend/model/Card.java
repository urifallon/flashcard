package com.flashcard.backend.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"folders", "userCardStats"})
@Table(name = "flashcards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String front;

    @Column(nullable = false)
    private String back;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String media;

    @Column(nullable = false)
    private int difficulty = 3;

    @Column(nullable = false)
    private int quality;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;

    @ManyToMany(mappedBy = "cards")
    private List<Folder> folders = new ArrayList<>();

    @OneToMany(mappedBy = "card")
    private List<UserCardStats> userCardStats = new ArrayList<>();


}
