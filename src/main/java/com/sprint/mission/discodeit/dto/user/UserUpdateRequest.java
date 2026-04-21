package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class UserUpdateRequest {
    @Size(max = 50, message = "변경할 이름은 50자 이하로 입력해주세요")
    private String newUsername;
    @Email(message = "이메일 형식이어야 합니다")
    private String newEmail;
    @Size(max = 60, message = "변경할 비밀번호는 60자 이하로 입력해주세요")
    private String newPassword;
}
