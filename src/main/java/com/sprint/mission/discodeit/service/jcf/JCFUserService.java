package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User createUser(String nickname, String email) {
        User user = new User(nickname, email);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(UUID userId) {
        User user = data.get(userId);
        if (user == null) {
            throw new RuntimeException("존재하지 않는 유저입니다.");
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUserNickname(UUID userId, String newNickname) {
        User user = findUserById(userId);
        return user.updateUserNickname(newNickname);
    }

    @Override
    public void deleteUser(UUID userId) {
        data.remove(userId);
    }

    @Override
    public Set<UUID> getJoinedChannels(UUID userId) {
        User user = findUserById(userId);
        return user.getChannels();
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        User user = findUserById(userId);
        user.join(channelId);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        User user = findUserById(userId);
        user.leave(channelId);
    }

    @Override
    public void leaveChannel(UUID channelId) {
        for (User user : data.values()) {
            user.leave(channelId);
        }
    }
}
