package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreateRequest {
  @NotBlank(message = "이름은 필수입니다.")
  @Size(min = 2, max = 20, message = "이름은 2자에서 20자 사이여야 합니다.")
  private String username;

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수입니다.")
  @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
  private String password;

  public UserCreateRequest(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }
}
