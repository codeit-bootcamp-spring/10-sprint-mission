package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final ArrayList<Message> messages = new ArrayList<>();      // 한 채널에서 발생한 메시지 리스트

    // 메시지 생성
    @Override
    public Message createMessage(String message, UUID userId, UUID channelId, MessageType type) {
        // 1. 유호성 검사

        // 2. 메시지 생성
        Message newMessage = new Message(message, userId, channelId, type);
        messages.add(newMessage);
        return newMessage;
    }

    // 2. 메시지 단건 조회
    @Override
    public Message searchMessage(UUID targetMessageId) {
        // 1. 메시지 탐색
        for (Message message : messages) {
            // 있으면 해당 메시지 반환
            if (message.getId().equals(targetMessageId)) {
                return message;
            }
        }
        // 없으면 널 반환
        System.out.println("해당 메시지가 존재하지 않습니다.");
        return null;
    }

    // 3. 메시지 다건 조회
    @Override
    public ArrayList<Message> searchMessageAll() {
        return messages;
    }

    // 메시지 수정
    @Override
    public void updateMessage(UUID targetMessageId, String newMessage) {
        // 1. 메시지 조회
        Message targetMessage = searchMessage(targetMessageId);

        // 2. 메시지 수정
        if ((targetMessage != null && newMessage != null) || newMessage.isBlank()) {
            targetMessage.updateMessage(newMessage);
        }
        else {
            System.out.println("잘못된 메시지입니다.");
        }
    }

    // 메시지 삭제
    @Override
    public void deleteMessage(UUID targetMessageId) {
        // 1. 메시지 조회
        Message targetMessage = searchMessage(targetMessageId);

        // 2. 메시지 삭제
        if (targetMessage != null) {
            messages.remove(targetMessage);
        }
    }
}
