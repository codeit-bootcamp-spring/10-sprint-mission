package com.sprint.mission.descodeit.entity;
import java.util.UUID;

public class Message extends BaseEntity{
    private UUID userId;
    private String text;
    private UUID channelId;

    public Message(UUID userId, String text, UUID channelId) {
        this.userId = userId;
        this.text = text;
        this.channelId = channelId;
    }

    public UUID getUserId(){
        return userId;
    }

    public String getText(){
        return text;
    }

    public UUID getChannelId(){
        return channelId;
    }

    public void addUser(UUID userId){
        if(this.userId == null) this.userId = userId;
    }

    public void addChannel(UUID channelId){
        if(this.channelId == null) this.channelId = channelId;
    }

    public void updateMessage(String newText){
        this.text = newText;
        updateTimeStamp();
    }

    @Override
    public String toString() {
        return this.getText();
    }
}
