package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private UserService userService;
    private MessageService messageService;
    private final List<Channel> data = new ArrayList<>();

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Channel createChannel(String channelName, ChannelType channelType, String description) {
        validateChannelExist(channelName);
        Channel channel = new Channel(channelName, channelType, description);
        data.add(channel);
        return channel;
    }


    @Override
    public Channel getChannel(UUID channelId) {
        return data.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));
    }

    @Override
    public List<Channel> getAllChannels() {
        return List.copyOf(data);
    }

    @Override
    public List<Message> getMessagesById(UUID channelId) {
        return getChannel(channelId).getMessages();
    }

    @Override
    public List<User> getUsersById(UUID channelId) {
        return getChannel(channelId).getUsers();
    }

    @Override
    public Channel updateChannel(UUID channelId, String channelName, ChannelType channelType, String description) {
        validateChannelExist(channelName);
        Channel findChannel = getChannel(channelId);
        Optional.ofNullable(channelName)
                .ifPresent(findChannel::updateChannelName);
        Optional.ofNullable(channelType)
                .ifPresent(findChannel::updateChannelType);
        Optional.ofNullable(description)
                .ifPresent(findChannel::updateDescription);
        return findChannel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel target = getChannel(channelId);
        target.getMessages().forEach(message -> messageService.deleteMessage(message.getId()));
        target.getUsers().forEach(user -> leaveChannel(channelId, user.getId()));
        data.remove(target);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userService.getUser(userId);

        channel.addUser(user);
        user.addChannel(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userService.getUser(userId);

        channel.removeUser(user);
        user.removeChannel(channel);
    }

    private void validateChannelExist(String channelName) {
        Optional<Channel> target = data.stream()
                .filter(c -> c.getChannelName().equals(channelName))
                .findFirst();
        if(target.isPresent()) throw new IllegalStateException("이미 존재하는 채널 이름입니다.");
    }
}
