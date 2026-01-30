package com.sprint.mission.discodeit.entity;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Message extends BaseEntity{
    private UUID userId;
    private String text;
    private UUID channelId;

    public Message(UUID userId, String text, UUID channelId) {
        this.userId = userId;
        this.text = text;
        this.channelId = channelId;
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
