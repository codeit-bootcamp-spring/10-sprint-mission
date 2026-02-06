package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private final User user;
    private final Channel channel;
    private String content;

    public Message(User user, Channel channel, String content) {
        if (user == null) throw new IllegalArgumentException("유저 정보가 유효하지 않습니다.");
        if (channel == null) throw new IllegalArgumentException("채널 정보가 유효하지 않습니다.");

        this.user = user;
        this.channel = channel;
        this.content = content;
        validateMessage();

        user.addMyMessages(this);
        channel.addMessage(this);
    }

    public void updateMessage(String content) {
        this.content = content;
        validateMessage();
        super.update();
    }

    // 메세지 생성 및 수정 시 준수해야 할 비즈니스 정책 (Fail-Fast)
    // content.length() < 1는 항상 false를 반환하므로 작성 하지 않음
    private void validateMessage() {

        // null, Blank 체크
        if (content == null || content.isBlank()) throw new IllegalArgumentException("메세지 내용을 입력해주세요");

        // 메세지 길이 체크 (1자 이상, 500자 이하)
        if (content.length() > 500) throw new IllegalArgumentException("메세지는 1자 이상, 500자 이하로 작성해주세요.");
    }

    @Override
    public String toString() {
        return String.format("Message[내용: %s, 작성자: %s, 채널: %s, Message ID: %s]",
                content, user, channel, getId());
    }

    public String getContent() {
        return content;
    }

    public Channel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }
}
