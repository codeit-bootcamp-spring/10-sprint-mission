package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);

    Optional<ReadStatus> findById(UUID id);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId); // 특정 유저의 특정 채널 읽음 상태 조회

    List<ReadStatus> findAllByUserId(UUID userId); // 특정 유저가 참여 중인 모든 채널의 읽음 상태 조회

    List<ReadStatus> findAllByChannelId(UUID channelId); // 특정 채널에 참여 중인 모든 유저의 읽음 상태 조회

    void deleteById(UUID id);

    void deleteByChannelId(UUID channelId);
}
