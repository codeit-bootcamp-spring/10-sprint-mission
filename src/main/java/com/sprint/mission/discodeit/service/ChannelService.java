package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name);
    Channel find(UUID id);
    List<Channel> readAll();
    Channel update(UUID id, String name);
    void deleteChannel(UUID channelID);
    void deleteUser(UUID userID);
    void addMember(UUID userID, UUID channelID);
    void removeMember(UUID userID, UUID channelID);

    List<String> getMembers(UUID id);
    List<String> findJoinedChannels(UUID id);
    List<String> getMessages(UUID id);
}
