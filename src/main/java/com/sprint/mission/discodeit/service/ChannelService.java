package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.UUID;
import java.util.List;


public interface ChannelService {
    void create(Channel channel);

    Channel readById(UUID id);

    List<Channel> readAll();

    void update(Channel channel);

    void delete(UUID id);

    void validateChannelStatus(UUID channelId); // 검증
}