package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotNull(message = "참가자 목록은 필수 입력 사항입니다.")
    @NotEmpty(message = "최소 한 명 이상의 참가자가 필요합니다.")
    List<UUID> participantIds
) {

}
