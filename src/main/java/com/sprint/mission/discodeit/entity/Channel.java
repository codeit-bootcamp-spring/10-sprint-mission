package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends DefaultEntity {
    private String channelName;
    private String channelDescription;

    private List<Message> messages = new ArrayList<>();//메시지 리스트
    private Set<User> allowedUsers = new HashSet<>();//채널 사용 가능한 유저 목록, 비어있으면 공개채널

    public Channel(String channelName, String channelDescription) {
        this.channelName = channelName;
        this.channelDescription = channelDescription;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
        this.updatedAt = System.currentTimeMillis();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        if(isAllowedUser(message.getUser().getId())){
            messages.add(message);
            if(!message.getChannel().equals(this)){
                message.setChannel(this);
            }
        }
        else{
            throw new RuntimeException("User not allowed to send message in this channel");
        }

    }

    public void DeleteMessage(Message message) {
        messages.remove(message);
    }

    public void replaceMessages(ArrayList<Message> newArrayList) {
        messages = newArrayList;
    }

    public String toString() {
        return "채널명: " + channelName + ", 설명: " + channelDescription;
    }

    public boolean isAllowedUser(UUID userID) {
        if(allowedUsers.isEmpty()){
            return true; //허용 리스트에 아무도 없으면 전원허용으로 간주함.
        }

       return allowedUsers.stream()
                .anyMatch(user -> userID.equals(user.getId()));
    }

    public void addAllowedUser(User user) {
        allowedUsers.add(user);
    }

    public void addAllowedUser(RoleGroup group) {
        group.getUsers()
                .forEach(this::addAllowedUser);
    }

    public void removeAllowedUser(User user) {
        allowedUsers.remove(user);
    }

    public void removeAllowedUser(RoleGroup group) {
        group.getUsers()
                .forEach(this::removeAllowedUser);
    }

    public Set<User> getAllowedUsers() {
        Set<User> users = new HashSet<>();
        users.addAll(allowedUsers);
        return users;
    }
}