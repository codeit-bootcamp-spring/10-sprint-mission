package com.sprint.mission.discodeit.event;

import java.util.UUID;

// 메시지 생성 이벤트
public record MessageCreatedEvent(
    UUID messageId,
    UUID channelId,
    UUID authorId,
    String authorName,
    String channelName,
    String content
) {

}