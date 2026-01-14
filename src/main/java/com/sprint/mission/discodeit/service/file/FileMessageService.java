package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements ChannelService, Serializable {
    private static final long serialVersionUID = 1L;
    private ChannelService channelService;
    private UserService userService;

    @Override
    public void setMessageService(MessageService messageService) {

    }

    // Setter
    @Override
    public void setUserService(UserService userService) {

    }

    @Override
    public Channel create(String name) {
        return null;
    }

    @Override
    public Channel find(UUID id) {
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return List.of();
    }

    @Override
    public Channel updateName(UUID channelID, String name) {
        return null;
    }

    @Override
    public void deleteChannel(UUID channelID) {

    }

    @Override
    public void joinChannel(UUID userID, UUID channelID) {

    }

    @Override
    public void leaveChannel(UUID userID, UUID channelID) {

    }

    @Override
    public List<String> findMembers(UUID channelID) {
        return List.of();
    }
}
