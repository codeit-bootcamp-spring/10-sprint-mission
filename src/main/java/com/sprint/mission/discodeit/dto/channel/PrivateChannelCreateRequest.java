package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        @NotNull(message = "참여자 목록을 확인할 수 없습니다.")
        @Size(min = 1, message = "참여자가 1명 이상 필요합니다.")
        List<@NotNull(message = "참여자 ID를 확인할 수 없습니다.") UUID> participantIds
) {

}
