package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.UUID;

public class Channel extends defaultEntity{
    private String channelName;
    private String channelDescription;

    private ArrayList<Message> messages = new ArrayList<>();

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

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
        if(!message.getChannel().equals(this)){
            message.setChannel(this);
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
}