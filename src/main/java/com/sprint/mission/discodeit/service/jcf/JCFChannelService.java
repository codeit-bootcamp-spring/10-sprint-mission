package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> channelData;
    private final UserService userService;
    public JCFChannelService(UserService userService) {
        this.channelData = new ArrayList<>();
        this.userService = userService;
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        channelData.add(channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        return channelData.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));
    }

    @Override
    public List<Channel> readAll(){
        return channelData;
    } // realALl이랑 read랑 type 맞춰줘야 하나?

    @Override
    public void update(UUID id, String name) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        Channel channel = read(id);
        channel.updateName(name);
    }

    // 삭제가 잘되지 않았음
    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        Channel channel = read(id);
        channelData.remove(channel);
    }

    @Override
    public void addMember(UUID userID, UUID channelID){
        if (channelID == null || userID == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        Channel channel = read(channelID);
        User user = userService.read(userID);

        if (channel == null | user == null){
            throw new IllegalArgumentException("id must not be null");
        }

        channel.addMember(user);
        user.joinChannel(channel);

    }

    @Override
    public void removeMember(UUID userID, UUID channelID){
        if (channelID == null || userID == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        Channel channel = read(channelID);
        User user = userService.read(userID);

        channel.removeMembersIDs(user);
        user.leaveChannel(channel);
    }
}
