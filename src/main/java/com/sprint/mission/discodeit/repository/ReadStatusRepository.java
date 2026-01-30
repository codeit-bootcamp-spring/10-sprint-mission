package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    ReadStatus save(ReadStatus readStatus);//사용자가 채널에 참가할때등등
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
//    List<ReadStatus> findAll();
    boolean existsById(UUID userId,UUID channelId);
    void deleteById(UUID userId,UUID channelId);

    //특정 채널에서의 userId들
    List<UUID> findUserIdsByChannelId(UUID channelId);
    //특정 유저의 channelId들
    List<UUID> findChannelIdsByUserId(UUID userId);
    //특정 채널의 readStatus 삭제
    void deleteByChannelId(UUID channelId);
}
