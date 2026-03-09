package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    // 사용자 상태 -> userStatus는 자식 엔티티
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus userStatus;

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

    public void updateStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
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
