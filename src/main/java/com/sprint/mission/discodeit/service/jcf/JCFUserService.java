package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> userMap =  new LinkedHashMap<>();

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

    // Create
    @Override
    public User createUser(String username, String nickname, String email, String phoneNumber) {
        // 사용자 명이 null이거나 비었는지는 엔티티에서 검증함

        if (findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + username);
        }

        checkDuplicatePhoneNumber(phoneNumber, null);
        checkDuplicateEmail(email, null);

        User newUser = new User(username, nickname, email, phoneNumber);
        userMap.put(newUser.getId(), newUser);

        return newUser;
    }

    // Read
    @Override
    public User findById(UUID userId) {
        User user = userMap.get(userId);
        if (user == null) {
            throw new NoSuchElementException("사용자를 찾을 수 없습니다: " + userId);
        }
        return user;

    }

    @Override
    public Optional<User> findByUsername(String username) {
        // Map의 값(Values)들을 Stream으로 순회하며 찾기
        return userMap.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }


    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    // Update - Profile

    @Override
    public User updateUserProfile(UUID userId, String newUsername, String newNickname, String newEmail, String newPhoneNumber) {
        User user = findById(userId);

        Optional.ofNullable(newUsername)
                .filter(username -> !username.equals(user.getUsername()))
                .ifPresent(username -> {updateUsername(userId, username);});

        Optional.ofNullable(newNickname)
                .filter(nickname -> !nickname.equals(user.getNickname()))
                .ifPresent(nickname -> {updateNickname(userId, nickname);});

        Optional.ofNullable(newEmail)
                .filter(email -> !email.equals(user.getEmail().orElse(null)))
                .ifPresent(email -> {updateEmail(userId, email);});

        Optional.ofNullable(newPhoneNumber)
                .filter(phoneNumber -> !phoneNumber.equals(user.getPhoneNumber().orElse(null)))
                .ifPresent(phoneNumber -> {updatePhoneNumber(userId,  phoneNumber);});

        return user;
    }

    @Override
    public void updateUsername(UUID userId, String newUsername) {
        User user = findById(userId);
        if (findByUsername(newUsername).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + newUsername);
        }
        user.updateUsername(newUsername);

    }
    @Override
    public void updateNickname(UUID userId, String newNickname) {
        User user = findById(userId);
        user.updateNickname(newNickname);

    }

    @Override
    public void updateEmail(UUID userId, String email) {
        User user = findById(userId);
        checkDuplicateEmail(email, userId);
        user.updateEmail(email);
    }

    @Override
    public void updatePhoneNumber(UUID userId, String phoneNumber) {
        User user = findById(userId);
        checkDuplicatePhoneNumber(phoneNumber, userId);
        user.updatePhoneNumber(phoneNumber);
    }

    // Update - Status
    @Override
    public User updateUserStatus(UUID userId, User.UserPresence newPresence, Boolean newMicrophoneIsOn, Boolean newHeadsetIsOn) {
        User user = findById(userId);

        Optional.ofNullable(newPresence)
                .filter(presence -> !presence.equals(user.getPresence()))
                .ifPresent(presence -> {updatePresence(userId, presence);});

        Optional.ofNullable(newMicrophoneIsOn)
                .filter(microphoneIsOn -> !microphoneIsOn.equals(user.isMicrophoneOn()))
                .ifPresent(microphoneIsOn -> {toggleMicrophone(userId, microphoneIsOn);});

        Optional.ofNullable(newHeadsetIsOn)
                .filter(headsetIsOn -> !headsetIsOn.equals(user.isHeadsetOn()))
                .ifPresent(headsetIsOn -> {toggleHeadset(userId, headsetIsOn);});

        return user;
    }

    @Override
    public void updatePresence(UUID userId, User.UserPresence presence) {
        User user = findById(userId);
        user.changeStatus(presence);
    }

    @Override
    public void toggleMicrophone(UUID userId, boolean isOn) {
        User user = findById(userId);
        user.toggleMicrophone(isOn);
    }

    @Override
    public void toggleHeadset(UUID userId, boolean isOn) {
        User user = findById(userId);
        user.toggleHeadset(isOn);
    }

    // Delete
    @Override
    public User deleteUser(UUID userId) {
        User user = findById(userId);
        messageService.deleteMessagesByAuthorId(userId);
        user.getChannels().forEach(user::leaveChannel);
        userMap.remove(userId);

        return user;
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

    // Duplicate Check
    private void checkDuplicateEmail(String email, UUID excludeUserId){
        if (email == null) return;

        if(userMap.values().stream()
                .filter(u -> !u.getId().equals(excludeUserId)) // 본인 제외
                .anyMatch(u -> u.getEmail()
                        .map(e -> e.equals(email))
                        .orElse(false))) {
            throw new IllegalArgumentException("이미 사용 중인 이메일:" + email);
        };
    }

    private void checkDuplicatePhoneNumber(String phoneNumber, UUID excludeUserId){
        if (phoneNumber == null) return;

        if(userMap.values().stream()
                .filter(u -> !u.getId().equals(excludeUserId)) // 본인 ID 제외
                .anyMatch(u -> u.getPhoneNumber()
                        .map(e -> e.equals(phoneNumber))
                        .orElse(false))) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호: " + phoneNumber);
        }
    }
}