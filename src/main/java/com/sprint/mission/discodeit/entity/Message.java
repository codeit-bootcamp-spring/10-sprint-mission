package com.sprint.mission.discodeit.entity;

import java.time.format.DateTimeFormatter;

public class Message extends Basic{

    private String messageContent;
    private User sender; // 보내는 사람을 User 타입으로 받음.
    private Channel channel;

    public Message(String content, User sender, Channel channel) {
        super(); // Message 에도 역시 각각 고유 ID, 생성 시간 할당.
        this.messageContent = content;
        setSender(sender);
        setChannel(channel);
    }

    public String getContent() {
        return messageContent;
    }

    public User getSender() {
        return sender;
    }

    public Channel getChannel() {
        return channel;
    }

    public void update(String content) {
        this.messageContent = content;
        update();
    }

    // user 와 message 의 동기화 필수
    public void setSender(User sender) {
        this.sender = sender;
        // 반대쪽(User)에 내가 없으면 추가
        if (!sender.getMessages().contains(this)) { //sender = 보낸 쪽에서의 List<Message>에서 리스트를 가져오고
            //거기서 contain(this) 현재 자신의 메세지 객체가 포함되어 있지 않으면, sender의 addMessage 호출!
            sender.addMessage(this);
        }
    }

    //channel 과의 동기화 필수...
    public void setChannel(Channel channel) {
        this.channel = channel;
        if (channel != null && !channel.getMessages().contains(this)) {
            channel.addMessages(this);
        }
    }

    @Override
    public String toString() {
        return "[" + getCreatedAt() + "] "  // Basic에서 상속받은 포맷된 시간
                + getSender().getUserName() + ": "
                + getContent();
    }
}
