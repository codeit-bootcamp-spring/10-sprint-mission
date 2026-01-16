package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
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
    public User createUser(String username, String email) {
        User user = new User(username, email);
        data.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public User update(UUID id, String username, String email) {
        User user = data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        // username이 null이 아닐 때만 수정
        Optional.ofNullable(username).ifPresent(user::setUsername);

        // email이 null이 아닐 때만 수정
        Optional.ofNullable(email).ifPresent(user::setEmail);

        user.touch();  // updatedAt 갱신
        return user;
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));

        data.remove(user);
    }

}
