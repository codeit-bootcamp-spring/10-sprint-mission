package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.DiscordEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ChannelRepository {

    public void save(Channel channel);
    public Channel findByID(UUID uuid);
    public List<Channel> findAll();
    public List<Channel> load();
    public Channel delete(Channel channel);
}
