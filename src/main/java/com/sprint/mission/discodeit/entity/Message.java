package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends Base  {
    // 필드
    private String contents;
    private final User sender;
    private final Channel channel;
    private List<UUID> attachmentIDs;

    // 생성자
    public Message(String contents, User sender, Channel channel, List<UUID> attachmentIDs) {
        super();
        this.contents = contents;
        this.sender = sender;
        this.channel = channel;
        this.attachmentIDs = attachmentIDs;

    }
    // Setter
    public void updateContents(String contents) {
        this.contents = contents;
        updateUpdatedAt();
    }

    public void addAttachment(UUID attachmentID){
        attachmentIDs.add(attachmentID);
    }
}
