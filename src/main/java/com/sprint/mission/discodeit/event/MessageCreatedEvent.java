package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

// 채널에 새로운 메시지가 생성될 시 알림 발생을 요청하는 이벤트 클래스
@Getter
@NoArgsConstructor(force = true)
public class MessageCreatedEvent {

    private final UUID messageId;
    private final String messageContent;

    private final UUID channelId;
    private final ChannelType channelType;
    private final String channelName;

    private final UUID authorId;
    private final String authorName;

    // 이벤트 생성 시간
    private final Instant occurredAt;

    // 이벤트 생성자
    public MessageCreatedEvent(
            UUID messageId,
            String messageContent,
            UUID channelId,
            ChannelType channelType,
            String channelName,
            UUID authorId,
            String authorName
    ) {
        this.messageId = messageId;
        this.messageContent = messageContent;
        this.channelId = channelId;
        this.channelType = channelType;
        this.channelName = channelName;
        this.authorId = authorId;
        this.authorName = authorName;

        this.occurredAt = Instant.now();
    }
}
