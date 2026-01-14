package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Message extends BaseEntity {
    private final Channel messageChannel; // 메시지가 위치한 채널
    private final User author; // 메시지 작성자
    private String content; // 메시지 내용

    // 생성자
    public Message(Channel messageChannel, User author, String content) {
        this.messageChannel = messageChannel;
        this.author = author;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId = " + getId() + ", " +
                "messageChannel = " + messageChannel.getId() + ", " +
//                "createdAt = " + getCreatedAt() + ", " +
//                "updatedAt = " + getUpdatedAt() + ", " +
                "author = " + author.getId() + ", " +
                "content = " + content +
                "}";
    }

    // Getter
    public Channel getMessageChannel() {
        return messageChannel;
    }

    public User getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    // update
    public void updateContent(String content) {
        this.content = content;
        updateTime();
    }

    public void addUserWriteMessageList(User user, String content) {
        // 진행 중...
    }

    public void addChannelWriteMessageList(Channel channel, String content) {
        // 진행 중...
    }
}
