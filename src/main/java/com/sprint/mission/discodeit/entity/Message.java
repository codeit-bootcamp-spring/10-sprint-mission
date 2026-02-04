package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseDomain implements Serializable {
    // 필드
    private String msg;
    private UUID userId;
    private UUID channelId;
    private List<UUID> attachmentIds;

    private static final long serialVersionUID = 1L;

    // 생성자
    public Message(UUID channelId, UUID userId, String msg) {
        super();
        this.channelId = channelId;
        this.userId = userId;
        this.msg = msg;
        this.attachmentIds = new ArrayList<>();
    }
    public Message(UUID channelId, UUID userId, String msg, List<UUID> attachmentIds) {
        super();
        this.channelId = channelId;
        this.userId = userId;
        this.msg = msg;
        this.attachmentIds = attachmentIds;
    }


    // 메소드
//    public UUID getId() {
//        return this.id;
//    }
//
//    public long getCreatedAt() {
//        return this.createdAt;
//    }
//
//    public long getUpdatedAt() {
//        return this.updatedAt;
//    }
//
//    public User getUser() {
//        return this.user;
//    }
//
//    public Channel getChannel() {
//        return this.channel;
//    }

    public void updateText(String msg) {
        this.msg = msg;
        this.updatedAt = Instant.now();
    }

    public String toString() {
        return "이 메시지의 id: " + this.id + "\n"
                + "메시지 내용: " + this.msg + "\n"
                + "생성일: " + this.createdAt + "\n"
                + "변경일: " + this.updatedAt;
    }
}
