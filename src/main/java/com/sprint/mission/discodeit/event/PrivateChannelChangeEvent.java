package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.event.enums.ChangeType;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PrivateChannelChangeEvent {

    private final ChangeType changeType;
    private final ChannelDto channelDto;

    // 이벤트 생성 시간
    private final Instant occurredAt;

    // 이벤트 생성자
    public PrivateChannelChangeEvent(ChangeType changeType, ChannelDto channelDto) {
        this.changeType = changeType;
        this.channelDto = channelDto;

        this.occurredAt = Instant.now();
    }
}
