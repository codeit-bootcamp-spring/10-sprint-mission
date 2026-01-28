package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId); // 특정 유저가 특정 채널을 읽은 상태 조회

    void deleteByChannelId(UUID channelId); // 채널 삭제 시 모든 유저의 읽음 상태 삭제
}
