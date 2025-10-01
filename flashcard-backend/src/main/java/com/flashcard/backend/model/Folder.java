package com.flashcard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = {"user", "cards", "tags"})
@Table(name = "folders")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime created_at;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updated_at;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "folder_cards", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "folder_id"), // Khóa ngoại của Folder
            inverseJoinColumns = @JoinColumn(name = "card_id") // Khóa ngoại của Card
    )
    private List<Card> cards = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "folder_tags",
            joinColumns = @JoinColumn(name = "folder_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "folder")
    private List<UserFolderStats> userFolderStats = new ArrayList<>();
}
