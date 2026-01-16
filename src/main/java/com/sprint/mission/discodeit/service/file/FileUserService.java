package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ClearMemory;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class FileUserService implements UserService, ClearMemory {
    UserRepository userRepository;
    ChannelRepository channelRepository;
    MessageRepository messageRepository;

    public FileUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public User create(String name, UserStatus status) {
        userRepository.findByName(name)
                .ifPresent(u -> {
                    throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
                });
        User user = new User(name, status);
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자 ID입니다."));
        return user;
    }

    @Override
    public List<User> readAll() {
        return userRepository.readAll();
    }

    @Override
    public User update(UUID id, String newName, UserStatus newStatus) {
        User user = findById(id);
        user.updateName(newName);
        user.updateStatus(newStatus);
        return userRepository.save(user);
    }

    @Override
    public List<Message> getUserMessages(UUID id) {
        findById(id);
        return messageRepository.readAll().stream()
                .filter(msg -> msg.getSender().getId().equals(id))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<Channel> getUserChannels(UUID id) {
        findById(id);
        return channelRepository.readAll().stream()
                .filter(ch -> ch.getUsers().stream().anyMatch(u -> u.getId().equals(id)))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        userRepository.delete(id);
    }


    @Override
    public void clear() {
        userRepository.clear();
    }
}
