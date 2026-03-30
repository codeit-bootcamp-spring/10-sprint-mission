package com.sprint.mission.discodeit.dto.userstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "유저 정보 생성 요청")
public record UserStatusCreateRequest(
    @Schema(description = "유저 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
    UUID userId
) {

}
