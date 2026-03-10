package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor //기본 생성자
//@AllArgsConstructor //모든 필드를 담는 생성자 생성 -> 이건 ID값을 담으므로 JPA에서는 사용X

@Entity
@Table(name = "USERS")
public class User extends BaseUpdatableEntity{

    @Column(unique = true, nullable = false,length = 50)
    private String username;

    @Column(unique = true, nullable = false,length = 100)
    private String email;

    @Column(nullable = false,length = 60)
    private String password;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) //기존 등록된 프로필을 다른걸로 교체하거나, 지정을 안할시, BinaryContent 삭제
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true) //상대 Entity의 필드명
    private UserStatus status;


    public User(String username, String email, String password, BinaryContent profile) {
//        super();//id,createdAt 값 삽입(BseEntity)
        //
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
        if (newProfile != null && !newProfile.equals(this.profile)) {
            this.profile = newProfile;
        }
    }
}
