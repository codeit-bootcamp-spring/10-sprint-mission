package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> users;    // 유저 전체 목록

    public JCFUserService(){
        this.users = new HashMap<>();
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {   // 채널 참가
        User user = findById(userId);
        if (user != null) {
            user.joinChannel(channelId);
        }
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {   // 채널 나가기
        User user = findById(userId);
        if (user != null) {
            user.leaveChannel(channelId);
        }
    }

    @Override
    public void create(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void update(UUID id, String name, String email, String profileImageUrl, String status) {
        User user = findById(id);
        if (user != null)
            user.update(name, email, profileImageUrl, status);
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);
        if (user != null) {
            for (UUID channelId : user.getChannelIds()) {
            }
            users.remove(id);
        }
    }
}
