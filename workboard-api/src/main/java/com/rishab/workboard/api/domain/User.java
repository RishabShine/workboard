package com.rishab.workboard.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uq_users_email", columnNames = "email")
        }
)
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "profile_image_key")
    private String profileImageKey;

    @Column(name = "bio")
    private String bio;

    @Column(name = "created_at", nullable = false, insertable=false, updatable=false)
    private OffsetDateTime createdAt;

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (email != null) {
            email = email.toUpperCase();
        } if (profileImageKey != null) {
            profileImageKey = profileImageKey.toUpperCase();
        }
    }

}
