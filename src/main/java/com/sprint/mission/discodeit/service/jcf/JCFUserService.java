package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private Map<UUID, User> data = new HashMap<>();
    private ChannelService channelService;

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }
    public JCFUserService() {
        this.data = new HashMap<>();
    }

    public User create(String username) {
        User user = new User(username);
        data.put(user.getId(), user);
        return user;
    }

    public User findById(UUID userId) {
        User user = data.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    public User update(UUID userId, String username) {
        User user = findById(userId);
        user.updateUsername(username);
        return user;
    }

    public void delete(UUID userId) {
        findById(userId);
        data.remove(userId);
    }

    public List<User> findUsersByChannelId(UUID channelId) {
        Channel channel = channelService.findById(channelId);
        return channel.getUsers();
    }
}
