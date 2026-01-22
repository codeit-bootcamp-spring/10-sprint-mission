package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 메시지 생성
    public Message createMessage(String message, UUID userId, UUID channelId, MessageType type);

    // 메시지 단건 조회
    public Message searchMessage(UUID targetMessageId);

    // 메시지 전체 조회
    public List<Message> searchMessageAll();

    // 메시지 수정
    public Message updateMessage(UUID targetMessageId, String newMessage);

    // 메시지 삭제
    public void deleteMessage(UUID targetMessageId);
}
