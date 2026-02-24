package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserCreateMultipartRequest", description = "User 생성 multipart 요청")
public record UserCreateMultipartRequest(

    @Schema(description = "User 생성 정보(JSON)", requiredMode = Schema.RequiredMode.REQUIRED)
    UserCreateRequest userCreateRequest,

    @Schema(description = "User 프로필 이미지 파일", type = "string", format = "binary")
    byte[] profile
) {

}