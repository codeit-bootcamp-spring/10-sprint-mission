package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public User create(String name, String email) {
        User user = new User(name, email);
        data.add(user);
        return user;
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public User read(UUID userId) {
        return data.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow( () -> new NotFoundException("해당 ID의 유저를 찾을 수 없습니다"));
    }

    @Override
    public List<User> getUsersByChannel(UUID channelId) {
        return data.stream()
                .filter(user -> user.getChannelList().stream() // 유저는 여러 채널을 가질 수 있음
                        .anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public User update(UUID id, String name, String email) {
        User user = read(id);
        return user.update(name, email);
    }

    @Override
    public void delete(UUID id) {
        User user = read(id);
        data.remove(user);
    }

    @Override
    public void deleteUsersInChannel(UUID channelId) {
        List<User> userList = getUsersByChannel(channelId);
        for (User user : userList) {
            user.getChannelList().removeIf(channel -> channel.getId().equals(channelId));
        }

    }
}
