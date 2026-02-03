package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    void save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAll();
    void delete(UUID id);


    // 채널 삭제 시 관련 readstatus 정리용
    List<ReadStatus> findAllByChannelId(UUID channelId);
    // readstatusService 체크리스트
    List<ReadStatus> findAllByUserId(UUID userId);
    //  (userId, channelId) 중복 방지용
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

}
