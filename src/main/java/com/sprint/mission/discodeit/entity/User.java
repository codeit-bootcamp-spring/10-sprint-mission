package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;

import com.sprint.mission.discodeit.security.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    public User(String username, String email, String password, BinaryContent profile, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.role = role;
    }

    public void setProfile(BinaryContent profile) {
        this.profile = profile;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User update(String newUserName, BinaryContent newProfile, String newEmail,
            String newPassword) {
        boolean anyValueUpdated = false;
        if (newUserName != null && !newUserName.equals(this.username)) {
            this.username = newUserName;
            anyValueUpdated = true;
        }

        if (newProfile != null) {
            UUID currentProfileId = (this.profile != null) ? this.profile.getId() : null;
            UUID newProfileId = newProfile.getId(); // 여기서 null이 나올 수 있음

            if (!Objects.equals(newProfileId, currentProfileId)) {
                this.profile = newProfile;
                anyValueUpdated = true;
            }
        }

        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }

        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }

        return this;
    }

    @Override
    public String toString() {
        return "유저명 : " + username;
    }

}
