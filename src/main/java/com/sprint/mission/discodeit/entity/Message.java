package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {
    private String content;
    private final User sender;
    private final Channel channel;

    public Message(String content, User sender, Channel channel) {
        super();
        validateMessageContent(content);
        validateUser(sender);
        validateChannel(channel);

        this.content = content;
        this.sender = sender;
        this.channel = channel;
    }

    private void validateMessageContent(String content){
        if (content == null || content.trim().isEmpty()){
            throw new IllegalArgumentException("메시지의 내용은 null이거나 비어있을 수 없습니다.");
        }
    }
    private void validateChannel(Channel channel){
        if (channel == null){
            throw new IllegalArgumentException("메시지를 보내는 채널은 필수로 존재해야 합니다.");
        }
    }
    private void validateUser(User user){
        if (user == null){
            throw new IllegalArgumentException("메시지를 보내는 유저는 필수로 존재해야 합니다.");
        }
    }

    public String getContent() { return this.content; }
    public User getSender() { return this.sender; }
    public Channel getChannel() { return this.channel; }

    public void updateContent(String content){
        validateMessageContent(content);
        this.content = content;
        this.updateTimestamp();
    }
}