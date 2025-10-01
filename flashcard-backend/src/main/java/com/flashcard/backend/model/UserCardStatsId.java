package com.flashcard.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor

@EqualsAndHashCode(callSuper = false)
public class UserCardStatsId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "flashcard_id")
    private Long flashcardId;
}
