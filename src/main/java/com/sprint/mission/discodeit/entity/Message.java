package com.sprint.mission.discodeit.entity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity{
    private final UUID userId;
    private String text;
    private final UUID channelId;
    private final List<UUID> binaryContentList;

    public Message(UUID userId, String text, UUID channelId) {
        this.userId = userId;
        this.text = text;
        this.channelId = channelId;
        this.binaryContentList = new ArrayList<>();
    }

    public void addBinaryContent(UUID binaryContentId){
        binaryContentList.add(binaryContentId);
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
