package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> userMap;

    public JCFUserService() {
        this.userMap = new HashMap<>();
    }

    // Create
    @Override
    public User createUser(String username, String nickname, String email, String phoneNumber) {
        if (findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + username);
        }

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
    public void updateUsername(UUID id, String newUsername) {
        User user = getUserOrThrow(id);
        user.updateUsername(newUsername);
    }
    @Override
    public void updateNickname(UUID id, String newNickname) {
        User user = getUserOrThrow(id); // 존재 여부 확인
        user.updateNickname(newNickname); // 엔티티에게 로직 위임
    }

    @Override
    public void updateEmail(UUID id, String email) {
        User user = getUserOrThrow(id);
        user.updateEmail(email); // 엔티티의 방어 로직(폰 번호 확인 등)이 자동 수행됨
    }

    @Override
    public void updatePhoneNumber(UUID id, String phoneNumber) {
        User user = getUserOrThrow(id);
        user.updatePhoneNumber(phoneNumber);
    }

    // Update - Status
    @Override
    public void updateUserStatus(UUID userId, UserStatus status) {
        User user = getUserOrThrow(userId);
        user.changeStatus(status);
    }

    @Override
    public void toggleMicrophone(UUID userId, boolean isOn) {
        User user = getUserOrThrow(userId);
        user.toggleMicrophone(isOn);
    }

    @Override
    public void toggleHeadset(UUID userId, boolean isOn) {
        User user = getUserOrThrow(userId);
        user.toggleHeadset(isOn);
    }

    // Role Management
    @Override
    public void addRoleToUser(UUID userId, Role role) {
        User user = getUserOrThrow(userId);
        user.addRole(role.getId());
    }

    @Override
    public void removeRoleFromUser(UUID userId, Role role) {
        User user = getUserOrThrow(userId);
        user.removeRole(role.getId());
    }

    @Override
    public boolean hasRole(UUID userId, Role role) {
        User user = getUserOrThrow(userId);
        return user.hasRole(role.getId());
    }

    // Delete
    @Override
    public void deleteUser(UUID userId) {
        if (!userMap.containsKey(userId)) {
            throw new NoSuchElementException("삭제하려는 사용자를 찾을 수 없습니다. ID: " + userId);
        }
        userMap.remove(userId);
    }

    // Helper
    private User getUserOrThrow(UUID userId) {
        return findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. ID: " + userId));
    }
}