package com.flashcard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user", "folder"})
@Builder
@Table(name = "user_folder_stats")

public class UserFolderStats {

    @EmbeddedId
    private UserFolderStatsId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("folderId")
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @Column(nullable = false)
    private int times_studied;

    @Column(nullable = false)
    private int total_score;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime last_studied;
}
