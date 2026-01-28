package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;                    // 이메일 (변경 불가능)
    private String password;                 // 비밀번호 (변경 가능)
    private String nickname;                 // 닉네임 (변경 가능)
    private UUID profileId;                  // 사용자의 프로필 고유 id

    public User(String email, String password, String nickname) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.updatedAt = Instant.now();
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
        this.updatedAt = Instant.ofEpochSecond(System.currentTimeMillis());
    }
}
