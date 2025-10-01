package com.flashcard.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor

@EqualsAndHashCode
public class UserFolderStatsId implements Serializable {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "folder_id")
    private String folderId;
}
