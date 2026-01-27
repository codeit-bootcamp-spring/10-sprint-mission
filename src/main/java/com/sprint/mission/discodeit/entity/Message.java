package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class Message extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Channel channel; // 메시지가 위치한 채널
    private final User author; // 메시지 작성자
    private String content; // 메시지 내용

    // 생성자
    public Message(Channel channel, User author, String content) {
        this.channel = channel;
        this.author = author;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId = " + getId() + ", " +
                "channel = " + channel.getId() + ", " +
//                "createdAt = " + getCreatedAt() + ", " +
//                "updatedAt = " + getUpdatedAt() + ", " +
                "author = " + author.getId() + ", " +
                "content = " + content +
                "}";
    }

    // update
    public void updateContent(String content) {
        this.content = content;
        updateTime();
    }
}
