package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends CommonEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID authorId;
    private final UUID channelId;
    private String content;
    List<UUID> attachmentIds = new ArrayList<>();


    public Message(String content, UUID authorId, UUID channelId, List<UUID> attachmentIds) {
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
        this.attachmentIds = attachmentIds;

    }

    public void updateContent(String content) {
        this.content = content;
        update();
    }


}
