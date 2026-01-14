package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;


public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;
    public JCFChannelService(UserService userService) {
        this.userService = userService;
        this.data = new HashMap<>();
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
        validateChannelId(id);
        return data.get(id);
    }

    public List<Channel> getChannelsByUserId(UUID userId) {
        return data.values().stream()
                .filter(c -> c.getUsers().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }
    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannel(UUID id, String name, String type) {
        validateChannelId(id);
        Channel channel = data.get(id);
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
        validateChannelId(id);
        data.remove(id);
    }

    public void enterChannel(UUID userId, UUID channelId) {
        validateChannelId(channelId);
        User user = userService.getUser(userId);
        Channel channel = data.get(channelId);
        if(channel.getUsers().contains(user)) throw new IllegalArgumentException("이미 해당 채널에 참가 중입니다.");
        channel.addMember(user);
    }

    public void leaveChannel(UUID userId, UUID channelId) {
        validateChannelId(channelId);
        User user = userService.getUser(userId);
        Channel channel = data.get(channelId);
        if(!channel.getUsers().contains(user)) throw new IllegalArgumentException("참가하고 있지 않은 채널입니다.");
        channel.removeMember(user);
    }

    public void validateChannelInput(String name, String type){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("채널 이름은 필수입니다.");
        if (type == null || type.isBlank()) throw new IllegalArgumentException("채널 타입은 필수입니다.");
    }

    public void validateChannel(Channel channel) {
        if (channel == null || channel.getId() == null) {
            throw new IllegalArgumentException("채널 정보가 없습니다.");
        }
        if (data.get(channel.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
    }
    private void validateChannelId(UUID id){
        if (id == null) throw new IllegalArgumentException("채널 ID가 없습니다.");
        if (!data.containsKey(id)) throw new IllegalArgumentException("존재하지 않는 채널입니다.");
    }
}
