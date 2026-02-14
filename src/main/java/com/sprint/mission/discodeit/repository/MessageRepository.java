package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    // 메세지 저장
    void save(Message message);

    // 메시지 단건 조회
    Optional<Message> findById(UUID messageId);

    // 메시지 전체 조회
    List<Message> findAll();

    // 메시지 삭제
    void delete(Message message);
}
