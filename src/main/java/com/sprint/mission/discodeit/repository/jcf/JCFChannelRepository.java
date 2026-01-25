package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.AlreadyJoinedChannelException;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channels = new LinkedHashMap<>();
    private final UserService userService;

    public JCFChannelRepository(UserService userService) {
        this.userService = userService;
    }
    @Override
    public Channel createChannel(Channel channel) {
        channels.put(channel.getId(),  channel);
        return channel;
    }

    @Override
    public Channel findChannel(UUID id) {
        Channel channel = channels.get(id);

        if (channel == null) {
            throw new ChannelNotFoundException();
        }
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel removed =  channels.remove(channelId);
        if (removed == null) {
            throw new ChannelNotFoundException();
        }

    }

    @Override
    public boolean existsByNameChannel(String channelName) {
        return channels.values().stream()
                .anyMatch(c -> c.getChannelName().equals(channelName));
    }

    @Override
    public Channel findByUserId(UUID userId) {
        return channels.values().stream()
                .filter(c -> c.hasUserId(userId))
                .findFirst()
                .orElseThrow(ChannelNotFoundException::new);
    }

    public String findAllUserInChannel(UUID channelId) {
        Channel channel = channels.get(channelId);
        if (channel == null) {
            throw new ChannelNotFoundException();
        }

        List<User> users = channel.getChannelUser(); // 채널의 유저 리스트 가져오기
        if (users.isEmpty()) {
            return "(참여자 없음)";
        }

        // 이름만 콤마로 이어서 반환
        return users.stream()
                .map(User::getUserName)
                .collect(Collectors.joining(", "));
    }

    @Override
    public void userAddChannel(UUID channelId, UUID userId) {
        Channel channel = channels.get(channelId);
        if (channel == null) throw new ChannelNotFoundException();

        User user = userService.findUser(userId);
        if (channel.getChannelUser().contains(user)) throw new AlreadyJoinedChannelException();

        channel.getChannelUser().add(user);
    }
}