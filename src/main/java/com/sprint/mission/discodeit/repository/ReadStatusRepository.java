package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAll();
    void deleteById(UUID id);

    boolean existById(UUID id);
    // Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
    // TODO: 특정 채널의 메시지를 특정 유저가 읽었는지 확인할 때 필요할 수 있음 -> 비즈니스 로직 고려 후 판단
}
