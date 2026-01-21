package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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
    public User create(String username) {
        User newUser = new User(username);
        userRepository.save(newUser);
        return newUser;
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String username) {
        User user = userRepository.findById(userId);
        if (user != null) {
            user.updateUsername(username);
            userRepository.update(user);
            return user;
        }
        return null;
    }

    @Override
    public void delete(UUID userId) {
        userRepository.delete(userId);
    }

    @Override
    public List<User> findUsersByChannelId(UUID channelId) {
        return userRepository.findAll().stream()
                .filter(u -> u.getChannels().stream()
                        .anyMatch(c -> c.getId().equals(channelId)))
                .toList();
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        User user = userRepository.findById(userId);
        return (user != null) ? user.getMessages() : List.of();
    }
}