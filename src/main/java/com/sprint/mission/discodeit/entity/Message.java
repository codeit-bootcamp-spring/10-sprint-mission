package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@ToString(exclude = {"user", "channel"})
public class Message extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String content;
    private final User user;
    private final Channel channel;
    private boolean isEdited; // 수정 여부
    private boolean isPinned; // 고정 여부

    public Message(String content, User user, Channel channel) {
        super();
        this.content = content;
        this.user = user;
        this.channel = channel;
        this.isEdited = false;
        this.isPinned = false;
    }

    // 메시지 수정
    public void update(String content) {
        this.content = content;
        this.isEdited = true;
        this.updated();
    }

    // 메시지 고정/해제
    public void togglePin() {
        this.isPinned = !this.isPinned;
    }
}
