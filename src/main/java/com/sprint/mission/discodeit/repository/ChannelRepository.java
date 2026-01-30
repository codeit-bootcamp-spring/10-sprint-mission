package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);
    Optional<Channel> findById(UUID id);
    List<Channel> findAll();

    boolean existsById(UUID id);
    void deleteById(UUID id);

    //채널타입으로 채널 찾기
    List<Channel> findByChannelType(ChannelType channelType);
    //id들에 해당하는 채널 목록
    List<Channel> findByIds(List<UUID> channelIds);
}
