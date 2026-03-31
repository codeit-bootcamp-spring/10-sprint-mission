package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "UserCreateMultipartRequest", description = "User 생성 multipart 요청")
public record UserCreateMultipartRequest(

    @NotNull(message = "USER 생성 정보는 필수입니다.")
    @Valid
    @Schema(description = "User 생성 정보(JSON)", requiredMode = Schema.RequiredMode.REQUIRED)
    UserCreateRequest userCreateRequest,

    @NotNull(message = "파일 데이터는 필수입니다.")
    @Size(min = 1, message = "파일 데이터가 없습니다.")
    @Schema(description = "User 프로필 이미지 파일", type = "string", format = "binary")
    byte[] profile
) {

}
