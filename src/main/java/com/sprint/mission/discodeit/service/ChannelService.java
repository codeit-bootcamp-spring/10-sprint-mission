package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.UUID;

public interface ChannelService {
    public Channel createChannel(String channelName, User user, ChannelType channelType);
    public Channel searchChannel(UUID targetChannelId);
    public ArrayList<Channel> searchChannelAll();
    public void updateChannel(UUID targetChannelId, String newChannelName);
    public void deleteChannel(UUID targetChannelId);
    public void inviteMembers(UUID targetUserId, ArrayList<UUID> memberIds);
}