package com.teamzeu.velo.entities;

import com.teamzeu.velo.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users_tbl")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String avatar_url;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role;

    @Column(nullable = false)
    private Boolean isEmailVerified;
}
