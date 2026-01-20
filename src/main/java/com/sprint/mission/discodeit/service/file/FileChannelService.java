package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    @Override
    public Channel createChannel(String name, UUID ownerId) {
        return null;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return List.of();
    }

    @Override
    public Channel updateChannelName(UUID channelId, UUID userId, String name) {
        return null;
    }

    @Override
    public void deleteChannel(UUID channelId, UUID userId) {

    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {

    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {

    }

    @Override
    public List<User> getMembers(UUID channelId) {
        return List.of();
    }
}
