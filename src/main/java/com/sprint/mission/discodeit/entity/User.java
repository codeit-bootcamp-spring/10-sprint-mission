package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false, length = 60)
    private String password;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id", unique = true)
    private BinaryContent profile;

    // mappedBy = "user" -> UserStatus의 user가 관리하는 것(UserStatus가 연관관계의 주인)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;

    public User(String username, String email, String password, BinaryContent profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }


    public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
        }
        // 기존 프로필 이미지를 수정하거나 없었는데 추가 하려는 경우
        if (newProfile != null && !newProfile.equals(this.profile)) {
            this.profile = newProfile;
        }
        // 기존에 프로필 이미지가 있을 때 프로필 이미지를 없애려는 경우
        if (newProfile == null && this.profile != null) {
            this.profile = null;
        }
    }

    // UserStatus에 작성한 연관관계 편의 메소드에 필요한 status setter 메소드
    // default로 두어 UserStatus의 setUser() 사용을 서비스(외부)에서 사용을 강제하게끔 함
    void setStatus(UserStatus status) {
        this.status = status;
    }
}
