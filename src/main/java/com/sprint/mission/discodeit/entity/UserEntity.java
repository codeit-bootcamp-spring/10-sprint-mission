package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequestDTO;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;                    // 이메일 (변경 불가능)
    private String password;                 // 비밀번호 (변경 가능)
    private String nickname;                 // 닉네임 (변경 가능)
    private UUID profileId;                  // 사용자의 프로필 고유 id

    public UserEntity(UserCreateRequestDTO userCreateRequestDTO) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.email = userCreateRequestDTO.email();
        this.password = userCreateRequestDTO.password();
        this.nickname = userCreateRequestDTO.nickname();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.updatedAt = Instant.now();
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
        this.updatedAt = Instant.now();
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }
}
