package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public class Message extends Base implements Serializable {
    // 필드
    private static final long serialVersionUID = 1L;
    private String contents;
    private final User sender;
    private final Channel channel;

    // 생성자
    public Message(String contents, User sender, Channel channel) {
        super();
        this.contents = contents;
        this.sender = sender;
        this.channel = channel;

    }

    // Getter
    public String getContents() {
        return contents;
    }

    public User getSender() {
        return sender;
    }

    public Channel getChannel() {
        return channel;
    }

    // Setter
    public void updateContents(String contents) {
        this.contents = contents;
        update();
    }
}
