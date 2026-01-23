package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data = new LinkedHashMap<>();

    // 연관 도메인의 서비스
    private MessageService messageService;
    private ChannelService channelService;

    // Setter 주입, 순환 참조 문제 회피
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public void save(User user) {
        data.put(user.getId(), user);
    }

    // Create
    @Override
    public User createUser(String username, String nickname, String email, String phoneNumber) {
        // 사용자 명이 null이거나 비었는지는 엔티티에서 검증함

        if (findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + username);
        }

        if (data.values().stream()
                .anyMatch(u -> u.getEmail()
                        .map(e -> e.equals(email))
                        .orElse(false))) {
            throw new IllegalArgumentException("이미 사용 중인 이메일:" + email);
        }


        if (data.values().stream()
                .anyMatch(u -> u.getPhoneNumber()
                        .map(e -> e.equals(phoneNumber))
                        .orElse(false))) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호: " + phoneNumber);
        }

        User newUser = new User(username, nickname, email, phoneNumber);
        data.put(newUser.getId(), newUser);

        return newUser;
    }

    // Read
    @Override
    public User findById(UUID userId) {
        User user = data.get(userId);
        if (user == null) {
            throw new NoSuchElementException("사용자를 찾을 수 없습니다: " + userId);
        }
        return user;

    }

    @Override
    public Optional<User> findByUsername(String username) {
        // Map의 값(Values)들을 Stream으로 순회하며 찾기
        return data.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    // Update - Profile
    @Override
    public User updateUserProfile(UUID userId, String newUsername, String newNickname, String newEmail, String
            newPhoneNumber) {
        User user = findById(userId);

        if (isValid(user.getUsername(), newUsername)) user.updateUsername(newUsername);

        if (isValid(user.getNickname(), newUsername)) user.updateNickname(newNickname);

        if (isValid(user.getEmail(), newEmail)) user.updateEmail(newEmail);

        if (isValid(user.getPhoneNumber(), newPhoneNumber)) user.updatePhoneNumber(newPhoneNumber);

        return user;
    }


    // Update - Status
    @Override
    public User updateUserStatus(UUID userId, User.UserPresence newPresence, Boolean newMicrophoneIsOn, Boolean
            newHeadsetIsOn) {
        User user = findById(userId);

        if (isValid(user.getPresence(), newPresence)) user.changeStatus(newPresence);

        if (isValid(user.isMicrophoneOn(), newMicrophoneIsOn)) user.toggleMicrophone(newMicrophoneIsOn);

        if (isValid(user.isHeadsetOn(), newHeadsetIsOn)) user.toggleHeadset(newHeadsetIsOn);

        return user;
    }


    // Delete
    @Override
    public void deleteUser(UUID userId) {
        User user = findById(userId);
        messageService.deleteMessagesByAuthorId(userId);
        user.getChannels().forEach(user::leaveChannel);
        data.remove(userId);
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        User user = findById(userId);
        Channel channel = channelService.findById(channelId);
        user.joinChannel(channel);
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        User user = findById(userId);
        Channel channel = channelService.findById(channelId);
        user.leaveChannel(channel);
    }

    // Helper
    private boolean isValid(Object current, Object target) {
        // 1. 변경할 값(target)이 null이면 업데이트 하지 않음 (기존 Optional.ifPresent 역할)
        if (target == null) {
            return false;
        }

        // 2. 현재 값과 다를 때만 true 반환 (업데이트 진행)
        // Objects.equals는 내부적으로 null 체크를 해주므로 안전합니다.
        return !Objects.equals(current, target);
    }
}