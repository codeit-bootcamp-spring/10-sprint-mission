package com.sprint.mission.discodeit.entity;

<<<<<<< HEAD
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
    }

    public Channel getChannel() {
        return channel;
    }
    public String getContent() {
        return content;
    }
=======
import java.util.UUID;

public class Message extends BaseEntity{
    private String content;
    private Channel channel;
    private User user;
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f

    public User getUser() {
        return user;
    }

<<<<<<< HEAD


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
    }
=======
    public Channel getChannel() {
        return channel;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String newContent){
        this.content = newContent;
        super.setUpdatedAt(System.currentTimeMillis());
    }

    public Message(String content, Channel channel, User user) {
        this.content = content;
        this.channel = channel;
        this.user = user;
    }


>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
}
