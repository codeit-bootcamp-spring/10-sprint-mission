package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.UUID;

public interface ReadStatusRepository {
    // 읽음 상태 저장
    void save(ReadStatus readStatus);

    // 읽음 상태 단건 조회
    ReadStatus findById(UUID readStatusId);

    // 읽음 상태 삭제
    void delete(UUID readStatusId);
}
