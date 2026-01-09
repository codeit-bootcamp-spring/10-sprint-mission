package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    User user;
    final List<User> users = new ArrayList<>();

    @Override
    public void createUser(User newUser) {
        this.user = newUser;
        users.add(newUser);
    }

    @Override
    public List<User> getUserList() {
        return users.stream()
                .toList();
    }

    @Override
    public UUID getUserIdByName(String userName) {
        return users.stream()
                .filter(user -> user.getUserName().equals(userName))
                .findFirst()
                // 예외 처리
                .orElseThrow(() -> new IllegalArgumentException("해당 이름으로 된 유저가 존재하지 않습니다."))
                .getUserId();
    }

    // id 를 기준으로 수정
    @Override
    public void updateUserName(UUID userId, String newName) {
        users.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .ifPresent(user -> user.updateUsername(newName));
    }

    @Override
    public void deleteUser(UUID userId) {
        users.removeIf(user -> user.getUserId().equals(userId));
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId, String channelName) {

    }

    @Override
    public void listUserChannels(UUID userId) {

    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {

    }
}
