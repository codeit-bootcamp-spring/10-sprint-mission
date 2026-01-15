package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;


import java.util.*;

public class JCFChannelService implements ChannelService {

    final ArrayList<Channel> list;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.list = new ArrayList<>();
        this.userService = userService;
    }

    @Override
    public Channel createChannel(ChannelType type, String channelName, String channelDescription) {
            Validators.validationChannel(type, channelName, channelDescription);
            Channel channel = new Channel(type, channelName, channelDescription);
            list.add(channel);
            return channel;
    }

    @Override
    public Channel readChannel(UUID id) {
        return validateExistenceChannel(id);
    }

    @Override
    public List<Channel> readAllChannel() {
        return list;
    }

    @Override
    public Channel updateChannel(UUID id, ChannelType type, String channelName, String channelDescription) {
        Channel channel = validateExistenceChannel(id);
        Optional.ofNullable(type).ifPresent(t -> {Validators.requireNonNull(t, "type");
            channel.updateChannelType(t);
        });
        Optional.ofNullable(channelName)
                .ifPresent(name -> {Validators.requireNotBlank(name, "type");
            channel.updateChannelName(name);
        });
        Optional.ofNullable(channelDescription).ifPresent(des -> {
            Validators.requireNotBlank(des, "channelDescription");
            channel.updateChannelDescription(des);
        });

        return channel;
    }



    @Override
    public void deleteChannel(UUID id) {
        Channel channel = readChannel(id);
        list.remove(channel);
    }

    @Override
    public boolean isChannelDeleted(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return list.stream()
                .noneMatch(channel -> id.equals(channel.getId()));
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = readChannel(channelId);
        User user = userService.readUser(userId);

        boolean alreadyJoined = channel.getJoinedUsers().stream()
                .anyMatch(u -> userId.equals(u.getId()));

        if (alreadyJoined) {
            throw new IllegalArgumentException("이미 참가한 유저입니다.");
        }

        channel.getJoinedUsers().add(user);
        user.getJoinedChannels().add(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = readChannel(channelId);
        User user = userService.readUser(userId);

        boolean alreadyLeaved = channel.getJoinedUsers().stream()
                .noneMatch(u -> userId.equals(u.getId()));

        if (alreadyLeaved) {
            throw new IllegalArgumentException("이미 참가한 유저입니다.");
        }

        channel.getJoinedUsers().removeIf(u -> userId.equals(u.getId()));
        user.getJoinedChannels().removeIf(c -> channelId.equals(c.getId()));
    }

    @Override
    public List<Message> readMessagesByChannel(UUID channelId) {
        Channel channel = readChannel(channelId);
        return channel.getMessages();
    }

    @Override
    public List<User> readUsersByChannel(UUID channelId) {
        Channel channel = readChannel(channelId);
        return channel.getJoinedUsers();
    }

    private Channel validateExistenceChannel(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return list.stream()
                .filter(channel -> id.equals(channel.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
    }
}


