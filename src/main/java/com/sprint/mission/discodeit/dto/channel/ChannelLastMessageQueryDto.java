package com.sprint.mission.discodeit.dto.channel;

import java.time.Instant;
import java.util.UUID;

// 채널 ID와 시간을 묶는 DTO
// MessageRepository의 findLastMessagesByChannelIds 쿼리 결과를 담기 위한 용도로 사용
public record ChannelLastMessageQueryDto(
    UUID channelId,
    Instant lastMessageAt
) {

}
