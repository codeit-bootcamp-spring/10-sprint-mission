package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {
    // Getter
    // 필드
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "profile_id", columnDefinition = "uuid")
    private BinaryContent profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // 생성자
    public User(String username, String email, String password, BinaryContent profile) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        // default role = USER로 설정
        this.role = Role.USER;
    }

    // 비즈니스 메서드
    public void updateName(String name) {
        this.username = name;
        updateUpdatedAt();
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
}

