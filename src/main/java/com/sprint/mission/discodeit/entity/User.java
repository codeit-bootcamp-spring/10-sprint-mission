package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends BaseEntity {
    private String nickName;
    private String userName;
    private String email;
    private String phoneNumber;
    private Set<Channel> channels;
    private List<Message> messages;

    public User(String nickName, String userName, String email, String phoneNumber) {
        this.nickName = nickName;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        channels = new HashSet<>();
        messages = new ArrayList<>();
    }

    public String getNickName() {
        return nickName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Set<Channel> getChannels() {
        return channels;
    }

    public List<Message> getMessages() {
        return messages;
    }

    private void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private void setUserName(String userName) {
        this.userName = userName;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    private void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void update(
            String nickName,
            String userName,
            String email,
            String phoneNumber
    ) {
        Optional.ofNullable(nickName).ifPresent(this::setNickName);
        Optional.ofNullable(userName).ifPresent(this::setUserName);
        Optional.ofNullable(email).ifPresent(this::setEmail);
        Optional.ofNullable(phoneNumber).ifPresent(this::setPhoneNumber);

        markUpdated();
    }

    public void addChannel(Channel channel) {
        channels.add(channel);

        markUpdated();
    }

    public void addMessage(Message message) {
        if (!messages.contains(message)) {
            messages.add(message);
        }

        if (message.getSender() != this) {
            message.addUser(this);
        }

        markUpdated();
    }

    public void clear() {
        for (Message message : new ArrayList<>(messages)) {
            message.clear();
        }

        for (Channel channel : new ArrayList<>(channels)) {
            removeChannel(channel);
        }
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);

        if (channel.hasMember(this)) {
            channel.removeMember(this);
        }

        markUpdated();
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        markUpdated();
    }

    public boolean hasChannel(Channel channel) {
        return channels.contains(channel);
    }

    public void validateChannelOwner() {
        for (Channel channel : channels) {
            if (channel.isOwner(this)) {
                throw new IllegalArgumentException("소유한 채널있어 유저 삭제가 불가능합니다. userId: " + this.getId());
            }
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "nickName='" + nickName + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", channelsSize=" + channels.size() +
                ", messagesSize=" + messages.size() +
                '}';
    }

}
