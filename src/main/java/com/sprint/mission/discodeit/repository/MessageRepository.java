package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.MessageEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    // 메세지 저장
    void save(MessageEntity message);

    // 메시지 단건 조회
    Optional<MessageEntity> findById(UUID messageId);

    // 메시지 전체 조회
    List<MessageEntity> findAll();

    // 메시지 삭제
    void delete(MessageEntity message);

    // 특정 채널에서 마지막으로 발행된 메시지 시간 조회
    Instant getLastMessageAt(UUID channelId);
}
