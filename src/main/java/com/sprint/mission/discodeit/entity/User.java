package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends BaseEntity {
    private String username; // 사용자명
    private String nickname; // 별명
    private String email; // Nullable
    private String phoneNumber; // Nullable

    private UserStatus status; // 온라인, 오프라인, 자리비움
    private boolean isMicrophoneOn;
    private boolean isHeadsetOn;

    private final Set<Channel> channels = new HashSet<>();
    private final List<Message> messages = new ArrayList<>();


    public User(String username, String nickname, String email, String phoneNumber) {
        super();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("사용자명은 필수입니다.");
        }
        validateContact(email, phoneNumber);
        this.username = username;
        this.nickname = (nickname == null || nickname.isBlank()) ? username : nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;

        this.status = UserStatus.ONLINE;
        this.isMicrophoneOn = false;
        this.isHeadsetOn = false;
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

    public UserStatus getStatus() {return status;}
    public boolean isMicrophoneOn() {return isMicrophoneOn;}
    public boolean isHeadsetOn() {return isHeadsetOn;}

    // Update
    public void updateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("사용자명은 필수입니다.");
        }
        this.username = username;
        updateTimestamp();
    }
    public void updateNickname(String nickname) {
        this.nickname = (nickname == null || nickname.isBlank()) ? username : nickname;
        updateTimestamp();
    }
    public void updateEmail(String email) {
        validateContact(email, this.phoneNumber);
        this.email = email;
        updateTimestamp();
    }
    public void updatePhoneNumber(String phoneNumber) {
        validateContact(this.email, phoneNumber);
        this.phoneNumber = phoneNumber;
        updateTimestamp();
    }

    public void changeStatus(UserStatus status) {
        this.status = status;
    }
    public void toggleMicrophone(boolean isOn) {
        this.isMicrophoneOn = isOn;
    }
    public void toggleHeadset(boolean isOn) {
        this.isHeadsetOn = isOn;
    }

    // validation
    private void validateContact(String email, String phoneNumber) {
        if ((email == null || email.isBlank()) && (phoneNumber == null || phoneNumber.isBlank())) {
            throw new IllegalArgumentException("이메일이나 전화번호 둘 중 적어도 하나라도 있어야 함.");
        }
    }

    // Channel Control
    public void  joinChannel(Channel channel) {
        this.channels.add(channel);
        channel.addUser(this);
    }
    public void  leaveChannel(Channel channel) {
        this.channels.remove(channel);
        channel.removeUser(this);
    }
    public Set<Channel> getChannels() {
        return new HashSet<>(this.channels);
    }

    // Message Control
    public void addMessage(Message message) {
        this.messages.add(message);
    }
    public void removeMessage(Message message) {
        this.messages.remove(message);
    }
    public List<Message> getMessages() {
        return new ArrayList<>(this.messages);
    }

}
