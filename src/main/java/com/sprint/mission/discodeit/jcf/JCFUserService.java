package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    UserRepository userRepository;


    public JCFUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name, UserStatus status) {
        userRepository.findByName(name).ifPresent(u -> {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        });
        User newUser = new User(name, status);
        return userRepository.save(newUser);
    }

    @Override
    public User findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new NoSuchElementException("실패 : 존재하지 않는 사용자 ID입니다."));
        return user;
    }

    @Override
    public List<User> readAll() {
        return userRepository.readAll();
    }

    @Override
    public User update(UUID id, String newName, UserStatus newStatus) {
        User user = findById(id);   // 예외 검사

        user.updateName(newName);
        user.updateStatus(newStatus);

        return userRepository.save(user);
    }

    @Override
    public List<Message> getUserMessages(UUID id) {
        User user = findById(id);
        return user.getMessages();
    }

    @Override
    public List<Channel> getUserChannels(UUID id) {
        User user = findById(id);
        return user.getChannels();
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);   // 예외 검사

        userRepository.delete(id);
    }


}
