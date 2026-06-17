package com.sprint.mission.discodeit.event;

import java.util.UUID;

// on(MessageCreatedEvent) - 메시지 생성 시 이벤트 발행
public record MessageCreatedEvent(
    UUID messageId,
    UUID channelId,
    UUID authorId,
    String authorUsername,
    String content,
    String channelName
) {

}
