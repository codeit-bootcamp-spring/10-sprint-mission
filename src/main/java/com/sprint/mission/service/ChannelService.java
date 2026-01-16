package com.sprint.mission.service;

import com.sprint.mission.entity.Channel;

public interface ChannelService extends BaseService<Channel> {
    Channel create(String channelName);
}
