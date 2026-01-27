package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class Message extends Entity {
    private final Channel channel;
    private final User user;
    private String content;

    public Message(Channel channel, User user, String content) {
        super();
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
        // 마지막 수정 시각 갱신
        super.update();
        this.content = newContent;
        return this;
    }

    public String formatForDisplay() {
        // 메시지 출력 전용
        return "[" + channel.getName() + "] "
                + user.getNickname() + ": "
                + content;
    }

    @Override
    public String toString() {
        return String.format(
                "Message [id=%s, content=%s, channel=%s, user=%s]",
                getId().toString().substring(0, 5),
                content,
                "[id=" + channel.getId().toString().substring(0, 5) + ", " + channel.getName() + "]",
                "[id=" +  user.getId().toString().substring(0, 5) + ", " + user.getNickname() + "]"
        );
    }
}
