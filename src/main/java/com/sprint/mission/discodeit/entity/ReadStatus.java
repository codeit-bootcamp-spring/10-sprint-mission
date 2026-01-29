package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class ReadStatus extends BaseEntity{
    // 사용자가 처음 채널에 들어갔을 때 생성
    // 채녈 별 마지막 메세지 확인 시간
    @Serial
    private static final long serialVersionUID = 1L;

    // 사용자ID, 채널ID,
    private Instant lastReadTime = Instant.EPOCH;
    private final User user;
    private final Channel channel;

    // 사용자가 채널 확인한 시간 갱신
    public void updateLastReadTime(Instant lastReadTime) {
        this.lastReadTime = lastReadTime;
        updateTimestamp();
    }

    // 사용자가 메시지를 읽었는지 확인
    public boolean isReadMessage(Message message) {
        if (!this.channel.equals(message.getChannel()))
            throw new IllegalArgumentException("해당 채널의 메시지가 아닙니다. (Status Channel: "
                    + this.channel.getId() + ", Message Channel: " + message.getChannel().getId() + ")");
        return !message.createdAt.isAfter(lastReadTime);
    }
}
