package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
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
    public List<Channel> getChannelsByUserId(UUID userId) {
        List<Channel> result = new ArrayList<>();
        userService.getUser(userId)
                .getChannelIds()
                .forEach(channelId -> result.add(getChannel(channelId)));
        return result;
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
        Optional<Channel> deleteChannel = data.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findAny();
        if(deleteChannel.isEmpty()) return;
        Channel target = deleteChannel.get();
        target.getMessageIds().forEach(messageId -> messageService.deleteMessage(messageId));
        target.getUserIds().forEach(userId -> leaveChannel(channelId, userId));
        data.remove(target);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userService.getUser(userId);

        channel.addUserId(userId);
        user.addChannelId(channelId);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userService.getUser(userId);

        channel.removeUserId(userId);
        user.removeChannelId(channelId);
    }

    private void validateChannelExist(String channelName) {
        if(data.stream().anyMatch(c -> c.getChannelName().equals(channelName)))
            throw new IllegalStateException("이미 존재하는 채널 이름입니다.");
    }
}
