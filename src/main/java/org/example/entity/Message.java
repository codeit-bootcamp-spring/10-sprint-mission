package org.example.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private String content;
    private UUID authorId;
    private UUID channelId;

    public Message (String content, UUID authorId, UUID channelId) {
        super();
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    public void update (String content, UUID authorId, UUID channelId) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
//      this.authorId = authorId; 사용자 id랑 채널 id를 변경할 일이 없다고 판단
//      this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

}
