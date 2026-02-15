package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatusEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    // 읽음 상태 저장
    void save(ReadStatusEntity readStatus);

    // 읽음 상태 단건 조회
    Optional<ReadStatusEntity> findById(UUID readStatusId);

    // 읽음 상태 전체 조회
    List<ReadStatusEntity> findAll();

    // 읽음 상태 삭제
    void delete(ReadStatusEntity readStatus);

    // 유효성 검사 (중복 확인)
    Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
}
