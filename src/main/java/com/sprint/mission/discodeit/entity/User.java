package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
  // 자동으로 getter 메서드를 생성해준다. -> Lombok 적용
public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;

    // 프로필 이미지 id (User 1..0 BinaryContent)
    private UUID profileImageId;

    // 첨부파일이 있는 경우 (생성자 오버로드)
    public User(String username, String email, String password, UUID profileImageId) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImageId = profileImageId;
    }

    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileImageId) {
        boolean isAnyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            isAnyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            isAnyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            isAnyValueUpdated = true;
        }
        if (newProfileImageId != null && !newProfileImageId.equals(this.profileImageId)) {  // 프로픨 이미지 id는 null일 수 있음
            // TODO: null로 수정하는 것도 추가 -> 기존 등록된 사진 수정 로직 추가 필요 -> 관련 DTO 수정(UserUpdateRequest) + BasicUserService 수정 필요할 것으로 예쌍
            this.profileImageId = newProfileImageId;
            isAnyValueUpdated = true;
        }

        if (isAnyValueUpdated) {
            updateInstant();
        }
    }
}
