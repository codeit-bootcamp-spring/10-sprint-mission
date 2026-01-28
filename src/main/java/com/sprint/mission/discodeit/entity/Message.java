package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;

public class Message extends Base  {
    // 필드
    @Getter
    private String contents;
    @Getter
    private final User sender;
    @Getter
    private final Channel channel;

    // 생성자
    public Message(String contents, User sender, Channel channel) {
        super();
        this.contents = contents;
        this.sender = sender;
        this.channel = channel;

    }
    // Setter
    public void updateContents(String contents) {
        this.contents = contents;
        updateUpdatedAt();
    }
}
