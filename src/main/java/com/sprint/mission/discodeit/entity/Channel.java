package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.ChannelDto;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class Channel extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public enum channelType {PRIVATE, PUBLIC}
    // 채널에 속한 메세지 목록
    private final List<Message> messages = new ArrayList<>();
    // 채널에 참여한 유저 목록
    @Getter
    private final List<User> users = new ArrayList<>();

    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private final channelType type;
    @Getter
    private Instant lastMessageAt;

    // Public 생성자
    public Channel(ChannelDto.ChannelRequest request) {
        this.name = request.name();
        this.description = request.description();
        this.type = channelType.PUBLIC;
        this.lastMessageAt = Instant.now();
    }

    // Private 생성자
    public Channel(List<User> users){
        this.users.addAll(users);
        this.type = channelType.PRIVATE;
        this.lastMessageAt = Instant.now();
    }

    public void updateName(String name) {
        this.name = name;
        updateTimestamp();
    }

    public void updateDescription(String description) {
        this.description = description;
        updateTimestamp();
    }

    // 유저 목록에 유저 추가
    public void addUser(User user) {
        if (user == null)
            return;
        if (!this.users.contains(user))
            this.users.add(user);
    }

    // 유저 목록에서 유저 삭제
    public void removeUser(User user) {
        users.remove(user);
    }

    // 메세지 목록에 메세지 추가 + 채널 수정 시간 갱신
    public void addMessage(Message message) {
        this.messages.add(message);
        this.lastMessageAt = Instant.now();
        this.updateTimestamp();
    }

    // 메세지 목록에서 메세지 삭제 + 채널 수정 시간 갱신
    public void removeMessage(UUID messageId) {
        this.messages.removeIf(m -> m.getId().equals(messageId));
        this.updateTimestamp();
    }

    // 채널 메세지 리스트 반환
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}
