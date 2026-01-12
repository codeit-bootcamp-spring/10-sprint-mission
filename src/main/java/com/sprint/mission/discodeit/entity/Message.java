package com.sprint.mission.discodeit.entity;


import java.util.UUID;

public class Message extends Base {
    private String contents;
    UUID senderID;
    UUID channelID;

    public Message(String contents, UUID senderID, UUID channelID) {
        super();
        this.contents = contents;
        this.senderID = senderID;
        this.channelID = channelID;
    }

    public String getContents() {
        return contents;
    }

    public UUID getSenderID() {
        return senderID;
    }

    public UUID getChannelID() {
        return channelID;
    }

    public void updateContents(String contents) {
        this.contents = contents;
    }
}
