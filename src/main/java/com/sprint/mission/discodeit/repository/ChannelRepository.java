package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChannelEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    // 채널 저장
    void save(ChannelEntity channel);

    // 채널 단건 조회
    Optional<ChannelEntity> findById(UUID channelId);

    // 채널 전체 조회
    List<ChannelEntity> findAll();

    // 채널 삭제
    void delete(ChannelEntity channel);
}