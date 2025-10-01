package com.flashcard.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString(exclude = {"logs", "folders", "cards", "userCardStats", "userFolderStats"})
@Table(name = "users")

public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password_hash;

    @Column(nullable = false, updatable = false)
    private String role;

    @CreationTimestamp
    private LocalDateTime created_at;

    @CreationTimestamp
    private LocalDateTime updated_at;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return this.password_hash;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @OneToMany(mappedBy = "user")
    private List<AuditLog> logs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Folder> folders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Card> cards = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserCardStats>  userCardStats = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserFolderStats>  userFolderStats = new ArrayList<>();

}
