package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    // 읽음 상태 저장
    void save(ReadStatus readStatus);

    // 읽음 상태 단건 조회
    Optional<ReadStatus> findById(UUID readStatusId);

    // 읽음 상태 전체 조회
    List<ReadStatus> findAll();

    // 읽음 상태 삭제
    void delete(ReadStatus readStatus);

    // 유효성 검사 (중복 확인)
    Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
}
