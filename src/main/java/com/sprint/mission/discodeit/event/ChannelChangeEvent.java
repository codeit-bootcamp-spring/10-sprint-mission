package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.event.enums.ChangeType;
import lombok.Getter;

import java.time.Instant;

// 채널 정보 변경 시 알림 발생을 요청하는 이벤트 클래스
@Getter
public class ChannelChangeEvent {

    private final ChangeType changeType;
    private final ChannelDto channelDto;

    // 이벤트 생성 시간
    private final Instant occurredAt;

    // 이벤트 생성자
    public ChannelChangeEvent(ChangeType changeType, ChannelDto channelDto) {
        this.changeType = changeType;
        this.channelDto = channelDto;

        this.occurredAt = Instant.now();
    }
}
