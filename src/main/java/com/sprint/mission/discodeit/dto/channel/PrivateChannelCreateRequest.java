package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PrivateChannelCreateRequest {
    @NotNull(message = "개인 채널은 참여자가 필수입니다")
    private List<UUID> participantIds;
}
