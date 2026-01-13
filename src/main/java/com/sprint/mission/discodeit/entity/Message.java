package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {
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
    public void update(String text) {
        this.text = text;
        recordUpdate();
    }

}
