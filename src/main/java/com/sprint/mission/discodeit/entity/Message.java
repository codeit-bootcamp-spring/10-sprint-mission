package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content;
    private User user;
    private Channel channel;

    public Message(User user, String content, Channel channel) {
        super();
        this.user = user;
        this.content = content;
        this.channel = channel;
    }

    public void addUser(User user) {
        this.user = user;
        if (!user.getMessages().contains(this)) {
            user.getMessages().add(this);
        }
    }

    // 각 필드를 반환하는 getter
    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
    // 필드를 수정하는 update 함수
    public void updateContent(String content) {
        this.content = content;
        setUpdatedAt();
    }

    @Override
    public String toString() {
        return  "{content=" + content +
                ", user=" + user.getUserName() +
                ", channel=" + channel.getChannelName() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Message message)) return false;
        return Objects.equals(this.getId(), message.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }
}
