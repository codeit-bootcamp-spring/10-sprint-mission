package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseUpdatableEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "profile_id", unique = true)
    private BinaryContent profile; // 프로필 이미지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 생성자
    public User(
            String email,
            String username,
            String password,
            BinaryContent profile
    ) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.profile = profile;
        this.role = Role.USER; // 기본 권한
    }

    public void update(
            String username,
            String email,
            String password,
            BinaryContent profile
    ) {
        if (username != null) this.username = username;
        if (email != null) this.email = email;
        if (password != null) this.password = password;
        if (profile != null) this.profile = profile;
    }

    public void updateRole(Role role) {
        if (role != null) this.role = role;
    }
}
