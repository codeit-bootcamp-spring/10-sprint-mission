package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class User extends BaseEntity {
    private String username; // 사용자명
    private String nickname; // 별명
    private String email; // Nullable
    private String phoneNumber; // Nullable

    private Set<UUID> roleIds;
    private UserPresence presence; // 유저의 상태 관리


    public User(String username, String nickname, String email, String phoneNumber) {
        super();
        validateContact(email, phoneNumber);
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roleIds = new HashSet<>();
        this.presence = new UserPresence();
    }

    // Getter
    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public Set<UUID> getRoleIds() {
        return new HashSet<>(roleIds);
    }

    public UserPresence getPresence() {
        return presence;
    }

    // Update
    public void updateUsername(String username) {
        this.username = username;
        updateTimestamp();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
        updateTimestamp();
    }

    public void updateEmail(String email) {
        this.email = email;
        updateTimestamp();
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        updateTimestamp();
    }

    public void changeStatus(UserStatus status) {
        this.presence.changeStatus(status);
    }

    public void changeMicrophone(boolean isOn) {
        this.presence.changeMicrophone(isOn);
    }

    public void changeHeadset(boolean isOn) {
        this.presence.changeHeadset(isOn);
    }


    // Role
    public void addRole(UUID roleId) {
        this.roleIds.add(roleId);
    }

    public void removeRole(UUID roleId) {
        this.roleIds.remove(roleId);
    }

    // validation
    private void validateContact(String email, String phoneNumber) {
        if (email == null && phoneNumber == null) {
            throw new IllegalArgumentException("이메일이나 전화번호 둘 중 적어도 하나라도 있어야 함.");
        }
    }
}
