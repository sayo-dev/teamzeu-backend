package com.teamzeu.velo.user.model;

import com.teamzeu.velo.common.BaseEntity;
import com.teamzeu.velo.common.enums.VELO_ROLE;
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
    private VELO_ROLE role;

    @Column(nullable = false)
    private Boolean isEmailVerified;
}
