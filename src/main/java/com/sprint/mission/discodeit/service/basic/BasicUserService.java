package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository; // Needed for addChannel to save channel

    public BasicUserService(UserRepository userRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public User CreateUser(String userName, String email) {
        if (userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new IllegalArgumentException("이미 등록된 계정입니다. " + email);
        }
        User user = new User(userName, email);
        userRepository.save(user);
        System.out.println(user.getUserName() + "님의 계정 생성이 완료되었습니다.");
        return user;
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 계정은 존재하지 않습니다.\nID: " + userId));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 이메일이 등록되어 있지 않습니다."));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String userName, String email) {
        User foundUser = findById(userId);
        foundUser.update(userId, userName, email);
        userRepository.save(foundUser);
        return foundUser;
    }

    @Override
    public void delete(UUID userId) {
        userRepository.delete(userId);
    }

    @Override
    public void addMessage(UUID userId, Message msg) {
        User foundUser = findById(userId);
        foundUser.addMessage(msg);
        userRepository.save(foundUser);
    }

    @Override
    public void addChannel(UUID userId, Channel channel) {
        User foundUser = findById(userId);
        foundUser.addChannel(channel);
        userRepository.save(foundUser);
        channelRepository.save(channel); // Save the updated channel
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
