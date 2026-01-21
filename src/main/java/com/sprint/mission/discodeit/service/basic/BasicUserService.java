package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private MessageService messageService;

    public BasicUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public User createUser(String name, String email) {
        validateUserInput(name, email);
        User user = new User(name, email);
        userRepository.save(user);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID id, String name, String email) {
        User user = getUser(id);
        Optional.ofNullable(name).filter(n -> !n.isBlank()).ifPresent(user::updateName);
        Optional.ofNullable(email).filter(e -> !e.isBlank()).ifPresent(user::updateEmail);
        userRepository.save(user);
        user.getChannels().forEach(channel -> {
            channel.removeUser(user);
            channel.addUser(user);
            channelRepository.save(channel);
        });
        user.getMessages().forEach(messageRepository::save);
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        User user = getUser(id);
        if(messageService != null) {
            new ArrayList<>(user.getMessages()).forEach(m -> messageService.deleteMessage(m.getId()));
        }
        new ArrayList<>(user.getChannels()).forEach(channel -> {
            channel.removeUser(user);
            channelRepository.save(channel);
        });
        userRepository.delete(id);
    }

    private void validateUserInput(String name, String email){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("이름은 필수입니다.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("이메일은 필수입니다.");
    }
}
