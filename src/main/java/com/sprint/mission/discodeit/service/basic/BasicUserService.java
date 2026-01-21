package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private MessageService messageService;
    private ChannelService channelService;

    // 생성자를 통해 레포지토리 주입
    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 서비스 간 순환 참조 방지를 위한 Setter 주입
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public User createUser(String username, String nickname, String email, String phoneNumber) {
        User newUser = new User(username, nickname, email, phoneNumber);
        return userRepository.save(newUser);
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다: " + userId));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User updateUserProfile(UUID userId, String newUsername, String newNickname, String newEmail, String newPhoneNumber) {
        User user = findById(userId);

        if (isValid(user.getUsername(), newUsername)) user.updateUsername(newUsername);
        if (isValid(user.getNickname(), newNickname)) user.updateNickname(newNickname);
        if (isValid(user.getEmail().orElse(null), newEmail)) user.updateEmail(newEmail);
        if (isValid(user.getPhoneNumber().orElse(null), newPhoneNumber)) user.updatePhoneNumber(newPhoneNumber);

        userRepository.save(user);
        syncUserChanges(user);

        return user;
    }

    @Override
    public User updateUserStatus(UUID userId, User.UserPresence newPresence, Boolean newMicrophoneIsOn, Boolean newHeadsetIsOn) {
        User user = findById(userId);

        if (isValid(user.getPresence(), newPresence)) user.changeStatus(newPresence);
        if (isValid(user.isMicrophoneOn(), newMicrophoneIsOn)) user.toggleMicrophone(newMicrophoneIsOn);
        if (isValid(user.isHeadsetOn(), newHeadsetIsOn)) user.toggleHeadset(newHeadsetIsOn);

        userRepository.save(user);
        syncUserChanges(user);

        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findById(userId);

        if (messageService != null) {
            messageService.deleteMessagesByAuthorId(userId);
        }

        if (channelService != null) {
            user.getChannels().forEach(channel -> {
                user.leaveChannel(channel);
                channelService.save(channel);
            });
        }

        userRepository.deleteById(userId);
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        User user = findById(userId);
        Channel channel = channelService.findById(channelId);

        user.joinChannel(channel);

        channelService.save(channel);
        userRepository.save(user);
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        User user = findById(userId);
        Channel channel = channelService.findById(channelId);

        user.leaveChannel(channel);

        channelService.save(channel);
        userRepository.save(user);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    // Helper
    private boolean isValid(Object current, Object target) {
        if (target == null) return false;
        return !Objects.equals(current, target);
    }

    private void syncUserChanges(User user) {
        // 1. 유저가 작성한 모든 메시지 업데이트
        messageService.findMessagesByAuthor(user.getId()).forEach(msg -> {
            msg.updateAuthor(user);
            messageService.save(msg);
        });

        // 2. 유저가 가입한 모든 채널의 유저 목록 업데이트
        user.getChannels().forEach(channel -> {
            Channel lastestChannel = channelService.findById(channel.getId());
            lastestChannel.updateUserInSet(user);
            channelService.save(lastestChannel);
        });
    }
}