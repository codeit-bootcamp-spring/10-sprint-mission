package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseUpdatableEntity{
    @Column(name="username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    public User(String name, String email, String password, Role role){
        this.username = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void addProfileImage(BinaryContent profileImg){
        this.profile = profileImg;
    }

    public void updateName(String name){this.username = name;}

    public void updateEmail(String email){
        this.email = email;
    }

    public void updateProfileImg(BinaryContent profileImg){this.profile = profileImg;}

    public void updatePassword(String password){this.password = password;}

    public void updateRole(Role role){this.role = role;}

    @Override
    public String toString() {
        return username;
    }
}
