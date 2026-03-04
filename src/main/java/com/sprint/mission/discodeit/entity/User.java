package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor //기본 생성자
//@AllArgsConstructor //모든 필드를 담는 생성자 생성 -> 이건 ID값을 담으므로 JPA에서는 사용X

@Entity
@Table(name = "USERS")
public class User extends BaseEntity{

    private Instant updatedAt;
    //
    @Column(unique = true, nullable = false,length = 50)
    private String username;

    @Column(unique = true, nullable = false,length = 100)
    private String email;

    @Column(nullable = false,length = 60)
    private String password;

    private UUID profileId;     // BinaryContent

    public User(String username, String email, String password, UUID profileId) {
//        super();//id,createdAt 값 삽입(BseEntity)
        //
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }
        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
