package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public User create(String username, String email, String password) {
        existsByEmail(email);
        User user = new User(username, email, password);
        data.add(user);
        return user;
    }

    public User findUserById(UUID userId) {
        return data.stream()
                .filter(user -> user.getId().equals(userId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Override
    public User findUserByEmail(String email) {
        return data.stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    //특정 채널에 참가한 사용자 리스트 조회
    @Override
    public List<User> findUsersByChannel(UUID channelId) {
        return data.stream().filter(user -> user.getChannels().stream()
                        .anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public List<User> findAllUser() {
        return new ArrayList<>(data);
    }

    @Override
    public User update(UUID userId, String password, String username, String email) {
        existsByEmail(email);
        User user = findUserById(userId);
        validatePassword(user, password);
        Optional.ofNullable(username).ifPresent(user::updateUsername);
        Optional.ofNullable(email).ifPresent(user::updateEmail);
        return user;
    }

    @Override
    public User updatePassword(UUID userId, String currentPassword, String newPassword) {
        User user = findUserById(userId);
        validatePassword(user, currentPassword);
        user.updatePassword(newPassword);
        return user;
    }

    @Override
    public void delete(UUID userId, String password) {
        User user = findUserById(userId);
        validatePassword(user, password);

        new ArrayList<>(user.getChannels()).forEach(channel -> {
            channel.leave(user);
            user.leave(channel);
        });
        data.remove(user);
    }

    //유저 이메일 중복체크
    private void existsByEmail(String email) {
        boolean exist = data.stream().anyMatch(user -> user.getEmail().equals(email));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다: " + email);
        }
    }

    //비밀번호 검증
    private void validatePassword(User user, String inputPassword) {
        if (!user.getPassword().equals(inputPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
