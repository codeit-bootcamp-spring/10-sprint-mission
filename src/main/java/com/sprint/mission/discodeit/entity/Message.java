package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Message extends Base  {
    // 필드
    @Getter
    private String contents;
    @Getter
    private final User sender;
    @Getter
    private final Channel channel;
    @Getter
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
}
