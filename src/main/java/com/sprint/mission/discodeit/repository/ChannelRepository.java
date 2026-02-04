package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    // 채널 저장
    void save(Channel channel);

    // 채널 단건 조회
    Optional<Channel> findById(UUID channelId);

    // 채널 전체 조회
    List<Channel> findAll();

    // 채널 삭제
    void delete(Channel channel);
}