package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends BaseUpdateEntity {

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private BinaryContent profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User(String username, String email, String password, BinaryContent profile) {
        this(username, email, password, profile, Role.USER);
    }

    public User(String username, String email, String password, BinaryContent profile, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.role = role;
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
        if (newProfile != null && !newProfile.equals(this.profile)) {
            this.profile = newProfile;
        }
    }

    public void updateRole(Role newRole) {
        this.role = newRole;
    }

    // UserStatus와 User의 연관관계 편의 메서드
    public void setStatus(UserStatus status) {
        if (this.status != null) {
            this.status.setUser(null);
        }

        this.status = status;

        if (status != null && status.getUser() != this) {
            status.setUser(this);
        }
    }
}
