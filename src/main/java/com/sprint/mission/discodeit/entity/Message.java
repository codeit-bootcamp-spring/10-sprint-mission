package com.sprint.mission.discodeit.entity;

<<<<<<< HEAD
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    //
    private String content;
    //
    private UUID channelId;
    private UUID authorId;

    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().getEpochSecond();
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
=======
import java.io.Serializable;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private User user;
    private Channel channel;
    private String content;

    public Message(User user, Channel channel, String content) {
//        this.id = UUID.randomUUID();
//        this.createdAt = System.currentTimeMillis();
//        this.updatedAt = this.createdAt;
        super();
        this.user = user;
        this.channel = channel;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
>>>>>>> upstream/김혜성
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }
    public String getContent() {
        return content;
    }

<<<<<<< HEAD
    public UUID getChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now().getEpochSecond();
        }
=======
    public User getUser() {
        return user;
    }



    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "user=" + user +
                ", channel=" + channel +
                ", content='" + content + '\'' +
                '}';
>>>>>>> upstream/김혜성
    }
}
