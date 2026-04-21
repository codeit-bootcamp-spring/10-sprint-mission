package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserCreateRequest {
    @NotNull(message = "유저 이름은 null일 없습니다.")
    @Size(max = 50, message = "변경할 이름은 50자 이하로 입력해주세요")
    private String username;
    @Email(message = "이메일 형식이어야 합니다")
    private String email;
    @NotBlank(message = "비밀번호는 빈칸일 수 없습니다.")
    @Size(max = 60, message = "변경할 비밀번호는 60자 이하로 입력해주세요")
    private String password;
}
