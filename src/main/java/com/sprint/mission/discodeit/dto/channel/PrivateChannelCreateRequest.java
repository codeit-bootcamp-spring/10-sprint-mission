package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Schema(description = "Private 채널 생성 요청")
public record PrivateChannelCreateRequest(
    @NotEmpty
    @Schema(description = "채널 참여자 ID 목록", example = "[5cd294e0-4cde-4a67-8d5c-3f054927c595, 554fb7b8-8541-4310-8a03-693b1ffc13cf]")
    List<@NotNull UUID> participantIds
) {

}
