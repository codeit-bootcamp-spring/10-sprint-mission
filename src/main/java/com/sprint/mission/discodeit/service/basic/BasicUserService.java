package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name, String email, String password) {
        User user = new User(name, email, password);
        return userRepository.save(user);
    }

    @Override
    public User read(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 유저를 찾을 수 없습니다."));
    }

    @Override
    public List<User> readAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByChannel(UUID channelId) {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .filter(u -> u.getChannelList().stream()
                        .anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public User update(UUID id, String name, String email) {
        User user = read(id);
        user.update(name, email);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID id) {
        User user = read(id);
        userRepository.delete(id);

    }

    @Override
    public void deleteUsersInChannel(UUID channelId) {
        List<User> userList = userRepository.findAll();
        userList.forEach(user -> {
            boolean removed = user.getChannelList().removeIf(channel ->
                    channel.getId().equals(channelId));
            if(removed) {
                userRepository.save(user);
            }
        });

    }
}
