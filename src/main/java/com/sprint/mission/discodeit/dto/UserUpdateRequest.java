package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 수정 요청")
public class UserUpdateRequest {

  @Schema(description = "새로운 사용자 이름", example = "new_name")
  @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다.")
  private String newUsername;

  @Schema(description = "새로운 이메일 주소", example = "new_Test@gmail.com")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  @Size(max = 100)
  private String newEmail;

  @Schema(description = "새로운 비밀번호", example = "new_password1234")
  @Size(min = 8, max = 60, message = "비밀번호는 8~60자 사이여야 합니다.")
  private String newPassword;

  @AssertTrue(message = "사용자 이름은 공백일 수 없습니다.")
  public boolean isValidUsername() {
    return newUsername == null || !newUsername.isBlank();
  }

  @AssertTrue(message = "이메일은 공백일 수 없습니다.")
  public boolean isValidEmail() {
    return newEmail == null || !newEmail.isBlank();
  }
}
