package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final Channel channel;
    private final User user;
    private String content;

    public Message(Channel channel, User user, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.channel = channel;
        this.user = user;
        this.content = content;
    }

    public void addToChannelAndUser() {
        // 채널의 메시지 목록에 메시지 추가
        channel.addMessage(this);
        // 유저가 작성한 메시지 목록에 메시지 추가
        user.addMessage(this);
    }

    public void removeFromChannelAndUser() {
        // 채널의 메시지 목록에서 메시지 제거
        channel.removeMessage(this);
        // 유저가 작성한 메시지 목록에서 메시지 제거
        user.removeMessage(this);
    }

    public Message updateMessageContent(String newContent) {
        // 메시지 내용 변경
        this.content = newContent;
        // 수정 시각 갱신
        update();
        return this;
    }

    public String formatForDisplay() {
        // 메시지 출력 전용
        return "[" + channel.getName() + "] "
                + user.getNickname() + ": "
                + content;
    }

    public void update() {
        // 업데이트 시간 갱신
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return String.format(
                "Message [id=%s, content=%s, channel=%s, user=%s]",
                getId().toString().substring(0, 5),
                content,
                "[id=" + channel.getId().toString().substring(0, 5) + ", " + channel.getName() + "]",
                "[id=" + user.getId().toString().substring(0, 5) + ", " + user.getNickname() + "]"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
