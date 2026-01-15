package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends DefaultEntity {
    private String channelName;
    private String channelDescription;

    private List<Message> messages = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();

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
        boolean isAllowedUser = message.getUser()
                .getRoles()
                .stream()
                .anyMatch(g->g.getChannel().equals(this));
        if(isAllowedUser){
            messages.add(message);
            if(!message.getChannel().equals(this)){
                message.setChannel(this);
            }
        }
        else{
            throw new RuntimeException("User not allowed to send message in this channel");
        }

    }

    public void DeleteMessage(UUID messageID) {
        messages.stream()
                .filter(message -> messageID.equals(message.getId()))
                .findFirst()
                .ifPresent(messages::remove);
    }

    public void replaceMessages(ArrayList<Message> newArrayList) {
        messages = newArrayList;
    }

    public String toString() {
        return "채널명: " + channelName + ", 설명: " + channelDescription + ", 유저 목록: " + roles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void printChannel(){
        System.out.println("=========================");
        System.out.println(this);
        System.out.println("=========================");
        getMessages().forEach(System.out::println);
        System.out.println("=========================");
    }
}