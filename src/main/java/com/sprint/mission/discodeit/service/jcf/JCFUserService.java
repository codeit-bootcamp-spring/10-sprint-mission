package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> userMap =  new LinkedHashMap<>();

    // 연관 도메인의 서비스
    private MessageService messageService;

    // Setter 주입, 순환 참조 문제 회피
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
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
    public Optional<User> findById(UUID userId) {
        // Map.get()은 키가 없으면 null을 반환하므로 ofNullable 사용
        return Optional.ofNullable(userMap.get(userId));
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
    public User updateUsername(UUID id, String newUsername) {
        User user = getUserOrThrow(id);
        if (findByUsername(newUsername).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + newUsername);
        }
        user.updateUsername(newUsername);

        return user;
    }
    @Override
    public User updateNickname(UUID id, String newNickname) {
        User user = getUserOrThrow(id);
        user.updateNickname(newNickname);

        return user;
    }

    @Override
    public User updateEmail(UUID id, String email) {
        User user = getUserOrThrow(id);
        checkDuplicateEmail(email, id);
        user.updateEmail(email);

        return user;
    }

    @Override
    public User updatePhoneNumber(UUID id, String phoneNumber) {
        User user = getUserOrThrow(id);
        checkDuplicatePhoneNumber(phoneNumber, id);
        user.updatePhoneNumber(phoneNumber);

        return user;
    }

    // Update - Status
    @Override
    public User updateUserStatus(UUID userId, UserStatus status) {
        User user = getUserOrThrow(userId);
        user.changeStatus(status);

        return user;
    }

    @Override
    public User toggleMicrophone(UUID userId, boolean isOn) {
        User user = getUserOrThrow(userId);
        user.toggleMicrophone(isOn);

        return user;
    }

    @Override
    public User toggleHeadset(UUID userId, boolean isOn) {
        User user = getUserOrThrow(userId);
        user.toggleHeadset(isOn);

        return user;
    }

    // Delete
    @Override
    public User deleteUser(UUID userId) {
        User user = getUserOrThrow(userId);
        messageService.deleteMessagesByAuthorId(userId);
        user.getChannels().forEach(channel -> {channel.removeUser(user);});
        userMap.remove(userId);

        return user;
    }

    // Helper
    private User getUserOrThrow(UUID userId) {
        return findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    private void checkDuplicateEmail(String email, UUID excludeUserId){
        if (email == null) return;

        boolean exists = userMap.values().stream()
                .filter(u -> !u.getId().equals(excludeUserId)) // 본인 제외
                .anyMatch(u -> u.getEmail()
                        .map(e -> e.equals(email))
                        .orElse(false));

        if (exists) {
            throw new IllegalArgumentException("이미 사용 중인 이메일:" + email);
        }
    }

    private void checkDuplicatePhoneNumber(String phoneNumber, UUID excludeUserId){
        if (phoneNumber == null) return;

        boolean exists = userMap.values().stream()
                .filter(u -> !u.getId().equals(excludeUserId)) // 본인 제외
                .anyMatch(u -> u.getPhoneNumber()
                        .map(e -> e.equals(phoneNumber))
                        .orElse(false));

        if (exists) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호:" + phoneNumber);
        }
    }
}