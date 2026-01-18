package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;


public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;
    private MessageService messageService;
    public JCFChannelService(UserService userService) {
        this.userService = userService;
        this.data = new HashMap<>();
    }
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Channel createChannel(String name, String type) {
        validateChannelInput(name, type);
        Channel channel = new Channel(name, type);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        return validateChannelId(id);
    }
    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }
    @Override
    public List<Channel> getChannelsByUserId(UUID userId) {
        return data.values().stream()
                .filter(c -> c.getUsers().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }
    @Override
    public List<User> getUsersByChannelId(UUID channelId) {
        Channel channel = validateChannelId(channelId);
        return new ArrayList<>(channel.getUsers());
    }

    @Override
    public Channel updateChannel(UUID id, String name, String type) {
        Channel channel = validateChannelId(id);
        Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .ifPresent(channel::updateName);
        Optional.ofNullable(type)
                .filter(t -> !t.isBlank())
                .ifPresent(channel::updateType);
        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        Channel channel = validateChannelId(id);
        if (messageService != null) {
            new ArrayList<>(channel.getMessages()).forEach(m -> messageService.deleteMessage(m.getId()));
        }
        channel.getUsers().forEach(user -> user.removeChannel(channel));
        data.remove(id);
    }

    @Override
    public void enterChannel(UUID userId, UUID channelId) {
        User user = userService.getUser(userId);
        Channel channel = validateChannelId(channelId);
        if (channel.getUsers().contains(user)) throw new IllegalArgumentException("이미 해당 채널에 참가 중입니다.");

        channel.addUser(user);
        user.addChannel(channel);
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        User user = userService.getUser(userId);
        Channel channel = validateChannelId(channelId);
        if (!channel.getUsers().contains(user)) throw new IllegalArgumentException("참가하고 있지 않은 채널입니다.");
        if (messageService != null) {
            List<Message> targetMessages = channel.getMessages().stream()
                    .filter(m -> m.getUser().equals(user))
                    .toList();
            targetMessages.forEach(m -> messageService.deleteMessage(m.getId()));
        }
        channel.removeUser(user);
        user.removeChannel(channel);
    }

    public void validateChannelInput(String name, String type){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("채널 이름은 필수입니다.");
        if (type == null || type.isBlank()) throw new IllegalArgumentException("채널 타입은 필수입니다.");
    }
    private Channel validateChannelId(UUID id){
        return  Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다"));
    }
}
