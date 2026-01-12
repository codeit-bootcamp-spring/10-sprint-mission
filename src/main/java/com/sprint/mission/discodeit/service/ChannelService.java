package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void create(Channel channel);
    Channel read(UUID id);
    List<Channel> readAll();
    void update(UUID id, String name);
    void delete(UUID id);
}
