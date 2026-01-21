package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.file.Identifiable;

import java.io.Serializable;
import java.util.*;

public class User extends BaseEntity implements Serializable, Identifiable {
    private static final long serialVersionUID = 1L;

    public enum UserPresence {
        ONLINE,
        OFFLINE,
        AWAY,
    }

    private String username; // 사용자명
    private String nickname; // 별명
    private String email; // Nullable
    private String phoneNumber; // Nullable

    private UserPresence status; // 온라인, 오프라인, 자리비움
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

        this.status = UserPresence.ONLINE;
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

    public UserPresence getPresence() {return status;}
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

    public void changeStatus(UserPresence status) {
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
    public void joinChannel(Channel channel) {
        if (this.channels.contains(channel)) {
            return;
        }
        this.channels.add(channel);
        channel.addUser(this);
    }
    public void leaveChannel(Channel channel) {
        if (!this.channels.contains(channel)) {
            return;
        }
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
    public void updateMessageInList(Message updatedMessage) {
        for (int i = 0; i < this.messages.size(); i++) {
            // 리스트를 돌며 ID가 같은 메시지를 찾습니다.
            if (this.messages.get(i).getId().equals(updatedMessage.getId())) {
                // 해당 인덱스의 메시지 객체를 수정된 객체로 교체합니다.
                this.messages.set(i, updatedMessage);
                return;
            }
        }
    }

    // Convenience
    public void updateChannelInSet(Channel updatedChannel) {
        if (this.channels.contains(updatedChannel)) {
            this.channels.remove(updatedChannel); // 수정 전 객체 삭제
            this.channels.add(updatedChannel); // 수정 후 객체 등록
        }
    }



    // toString
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email=" + (email != null ? "'" + email + "'" : "null") +
                ", phoneNumber=" + (phoneNumber != null ? "'" + phoneNumber + "'" : "null") +
                ", status=" + status +
                ", isMicrophoneOn=" + isMicrophoneOn +
                ", isHeadsetOn=" + isHeadsetOn +
                ", channels=" + channels.size() +
                ", messages=" + messages.size() +
                '}';
    }

}
