package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    private ChannelService channelService;
    private MessageService messageService;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User create(String userName, String userEmail) {
        validateDuplicateEmail(userEmail);
        User newUser = new User(userName, userEmail);
        return userRepository.save(newUser);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + id));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findMembers(UUID channelId) {
        return channelService.findById(channelId).getMembers();
    }

    @Override
    public User update(UUID id, String userNickname) {
        User user = findById(id);
        user.updateNickname(userNickname);
        return userRepository.save(user);
    }

    @Override
    public void deleteUserByUserId(UUID userId) {
        User user = findById(userId);
        messageService.deleteMessagesByUserId(userId);
        new ArrayList<>(user.getMyChannels()).forEach(channel -> {
            channel.removeMember(user);
            channelService.save(channel);
        });
        userRepository.deleteById(userId);
    }

    // 이메일 중복 시 예외를 던져 가입 중단 (Fail-Fast)
    private void validateDuplicateEmail(String userEmail) {
        if (userRepository.findByEmail(userEmail).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + userEmail);
        }
    }
}
