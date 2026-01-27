package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String channelName;         // 채널 이름 (변경 가능)
    private User user;                  // 채널 소유자 (변경 불가능)
    private List<User> members;         // 채널 참가자 (변경 가능)
    private ChannelType type;           // CHAT, VOICE (변경 불가능)

    private List<Message> messages;     // 채널에서 발송된 메시지 목록

    public Channel(String channelName, User user, ChannelType channelType) {
        this.members = new ArrayList<>();
        this.messages = new ArrayList<>();

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.channelName = channelName;
        this.user = user;
        this.type = channelType;

        this.members.add(user);
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = Instant.now();
    }

    public void addMember(User user) {
        this.members.add(user);
    }

    public void removeMember(User user) {
        this.members.remove(user);
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void removeMessage(Message message) {
        this.messages.remove(message);
    }

}