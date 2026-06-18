package com.sprint.mission.discodeit.event.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
/// 새로운 메시지가 등록되면 이벤트를 발행하기위한 객체
@AllArgsConstructor
@Getter
public class MessageCreatedEvent {
     UUID messageId;
     UUID channelId;
}
