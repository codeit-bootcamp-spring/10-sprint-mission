package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.utils.Validation;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID channelId,
        String newName
) {
    public void validate(){
        if(channelId == null) throw new IllegalArgumentException("channelId는 null일 수 없습니다.");
        Validation.notBlank(newName, "채널이름");
    }
}
