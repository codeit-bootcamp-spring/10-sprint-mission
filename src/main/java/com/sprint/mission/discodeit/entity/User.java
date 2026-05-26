package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseUpdatableEntity {
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // 사용자의 프로필 이미지
    @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private BinaryContent profile;

    // 사용자 역할 지정 -> 유저 생성시 자동으로 USER 역할 할당
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    public User(String username, String email, String password, BinaryContent profile) {
        // id 자동생성 및 초기화
        super();
        // 필드 초기화
        this.username = username;
        this.email = email;
        this.password = password;
        // 사용자의 프로필 이미지
        this.profile = profile;
    }

    // username 수정 메서드
    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateProfile(BinaryContent profile) {
        this.profile = profile;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + super.getId() +
                ", createdAt=" + super.getCreatedAt() +
                ", updatedAt=" + super.getUpdatedAt() +
                ", username='" + username + '\'' +
                ", email=" + email +
                '}';
    }
}
