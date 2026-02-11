package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends Basic implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<UUID> attachmentIds = new ArrayList<>();
    private String messageContent;

    private UUID senderId;
    private UUID channelId;


    // 추가! ID 기반 생성자 (Service에서)
    public Message(String content, UUID senderId, UUID channelId) {
        super();
        this.messageContent = content;
        if(senderId==null||channelId==null) {
            throw new IllegalArgumentException("senderId / channelId null 불가");
        }
        this.senderId = senderId;
        this.channelId = channelId;
    }

    public void update(String content) {
        this.messageContent = content;
        update();
    }

//    // user 와 message 의 동기화 필수
//    public void setSender(User sender) {
//        this.sender = sender;
//        // 반대쪽(User)에 내가 없으면 추가
//        if (!sender.getMessages().contains(this)) { //sender = 보낸 쪽에서의 List<Message>에서 리스트를 가져오고
//            //거기서 contain(this) 현재 자신의 메세지 객체가 포함되어 있지 않으면, sender의 addMessage 호출!
//            sender.addMessage(this);
//        }
//    }
//
//    //channel 과의 동기화 필수...
//    public void setChannel(Channel channel) {
//        this.channel = channel;
//        if (channel != null && !channel.getMessages().contains(this)) {
//            channel.addMessages(this);
//        }
//    }

    // 메세지에 첨부파일 추가.
    public void addAttachment(UUID binaryContentId) {
        if (binaryContentId == null) throw new IllegalArgumentException("attachment id는 null 불가");
        if (!attachmentIds.contains(binaryContentId)) {
            attachmentIds.add(binaryContentId);
            update();
        }
    }

    @Override
    public String toString() {
        return "[" + getCreatedAt() + "] "  // Basic에서 상속받은 포맷된 시간
                + "(senderID = " + senderId+ ") "
                + messageContent;
    }
}
