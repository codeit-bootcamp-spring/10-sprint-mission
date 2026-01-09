package com.sprint.mission.service;

import com.sprint.mission.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void create(String name);

    Channel findById(UUID id);

    List<Channel> findAll();

    void update(UUID id, String name);

    void deleteById(UUID id);
}
