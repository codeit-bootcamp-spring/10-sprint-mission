package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.IsPrivate;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel create(String name, IsPrivate isPrivate, UUID ownerId){
        User owner = userService.findById(ownerId);
        Channel channel = new Channel(name, isPrivate, owner);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        validateExistence(data, id);
        return data.get(id);
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id) {
        validateExistence(data, id);
        Channel channel = findById(id);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        validateExistence(data, id);
        data.remove(id);
    }

    private void validateExistence(Map<UUID, Channel> data, UUID id){
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("실패 : 존재하지 않는 채널 ID입니다.");
        }
    }
}
