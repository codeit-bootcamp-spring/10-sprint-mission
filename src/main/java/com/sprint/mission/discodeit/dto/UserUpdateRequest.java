package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

  @Size(min = 2, max = 20, message = "이름은 2자에서 20자 사이여야 합니다.")
  private String newUsername;

  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String newEmail;

  @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
  private String newPassword;

  public UserUpdateRequest(String newUsername, String newEmail, String newPassword) {
    this.newUsername = newUsername;
    this.newEmail = newEmail;
    this.newPassword = newPassword;
  }
}
