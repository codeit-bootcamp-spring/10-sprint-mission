package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequestDTO;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusEntity extends BaseEntity {
    private UUID userId;                        // 사용자 고유 id (변경 불가능)
    private UUID channelId;                     // 채널 고유 id (변경 불가능)
    private ReadStatusType readStatusType;      // 읽음 상태 타입
    private Instant lastReadTime;               // 해당 채널에서 마지막으로 메시지를 읽은 시간 (변경 가능)

    public ReadStatusEntity(ReadStatusCreateRequestDTO readStatusCreateRequestDTO) {
        this.id = UUID.randomUUID();
        this.userId = readStatusCreateRequestDTO.getUserId();
        this.channelId = readStatusCreateRequestDTO.getChannelId();
        this.readStatusType = ReadStatusType.READ;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastReadTime = Instant.now();
    }

    public void updateLastReadTime() {
        this.lastReadTime = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateReadStatusType(ReadStatusType readStatusType) {
        this.readStatusType = readStatusType;
        this.updatedAt = Instant.now();
    }
}
