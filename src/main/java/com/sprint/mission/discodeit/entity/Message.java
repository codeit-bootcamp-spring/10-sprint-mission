package com.sprint.mission.discodeit.entity;


import java.util.HashMap;
import java.util.Map;

public class Message extends Base {
    // 필드
    private String contents;
    private final User sender;
    private final Channel channel;
    //Map<Channel, User> data;
    // 생성자
    public Message(String contents, User sender, Channel channel) {
        super();
        this.contents = contents;
        this.sender = sender;
        this.channel = channel;
        //this.data = new HashMap<>();
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
