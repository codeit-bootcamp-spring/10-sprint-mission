package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Channel channel;
    private final User sender;
    private String text;

    public Message(Channel channel, User sender, String text) {
        super();
        this.channel = channel;
        this.sender = sender;
        this.text = text;
    }

    // Getter 메소드
    public Channel getChannel() { return channel; }
    public User getSender() { return sender; }
    public String getText() { return text; }


    // update 메소드
    public Message update(String text) {
        this.text = text;
        recordUpdate();
        return this;
    }

}
