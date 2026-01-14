package org.example.service.jcf;

import org.example.entity.Channel;
import org.example.entity.Status;
import org.example.entity.User;
import org.example.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String username, String email, String password, String nickname) {
        User user = new User(username, email, password, nickname);
        data.put(user.getId(),user);
        return user;
    }

    @Override
    public User findById(UUID userId) {
        return Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NoSuchElementException("필드: id, 조건: 존재하는 유저, 값: " + userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }


    @Override
    public User update(UUID userId, String username, String email, String nickname, String password, Status status) {
        User user = findById(userId);

        // null이 아닌 값만 업데이트
        Optional.ofNullable(username).ifPresent(user::setUsername);
        Optional.ofNullable(email).ifPresent(user::setEmail);
        Optional.ofNullable(nickname).ifPresent(user::setNickname);
        Optional.ofNullable(password).ifPresent(user::setPassword);
        Optional.ofNullable(status).ifPresent(user::setStatus);

        return user;
    }

    /*@Override
    public User updateProfile(UUID userId, String username, String email, String nickname) {
        User user = findById(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setNickname(nickname);
        return user;
    }

    @Override
    public void changePassword(UUID userId, String newPassword) {
        User user = findById(userId);
        user.setPassword(newPassword);
    }

    @Override
    public void changeStatus(UUID userId, Status status) {
        User user = findById(userId);
        user.setStatus(status);
    }*/

    // 탈퇴 (Soft Delete) - 상태값 변경
    @Override
    public void softDelete(UUID userId) {
        User user = findById(userId);
        user.setStatus(Status.DELETED);
    }

    // 데이터 삭제
    @Override
    public void hardDelete(UUID userId) {
        User user = findById(userId);
        // 1. 참여한 채널 제거
        for (Channel channel : new ArrayList<>(user.getChannels())) {
            channel.getMembers().remove(user);
        }
        user.getChannels().clear();
        // 2. data 제거
        data.remove(userId);
    }

    @Override
    public List<Channel> findChannelByUser(UUID userId) {
        User user = data.get(userId);
        if(user == null){
            return List.of();
        }
        return user.getChannels();
    }
}
