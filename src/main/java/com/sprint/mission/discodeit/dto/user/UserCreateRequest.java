package com.sprint.mission.discodeit.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User 생성 정보")
public record UserCreateRequest(

    @NotBlank(message = "USERNAME은 필수입니다.")
    @JsonProperty("username")
    String userName,

    @NotBlank(message = "EMAIL은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "PASSWORD는 필수입니다.")
    String password,

    @JsonIgnore
    ProfileImageCreateRequest profileImage
) {

}
