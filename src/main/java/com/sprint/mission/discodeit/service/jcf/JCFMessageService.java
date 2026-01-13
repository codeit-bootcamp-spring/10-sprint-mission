package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.validation.ValidationMethods;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    public JCFMessageService(Map<UUID, Message> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JCFMessageService{" +
//                "data = " + data + ", " +
                "data key = " + data.keySet() + ", " +
                "data value = " + data.values() + ", " +
                "data size = " + data.size() + ", " +
                '}';
    }

    // C. 생성(메세지 작성)
    @Override
    public Message createMessage(Channel messageChannel, User author, String content) {
        // author가 존재하는 user인지 확인
        ValidationMethods.existUser(author);
        // messageChannel이 존재하는 channel인지 확인
        ValidationMethods.existChannel(messageChannel);
        // String `null` or `blank` 검증
        ValidationMethods.validateString(content, "content");

        Message message = new Message(messageChannel, author, content);

        // author가 해당 channel에 존재하는가? or 채널 입장 자체를 막기???
        if (!messageChannel.getChannelMembersList().contains(author)) {
            throw new IllegalArgumentException("author는 해당 channel에 참가하지 않았습니다.");
        }

        // 연관
        // channel 객체에 추가
        messageChannel.addChannelMessages(message);

        // author(user) 객체에 추가
        author.writeMessage(message);

        data.put(message.getId(), message);
        return message;
    }

    // R. 읽기
    // 특정 메시지 정보 읽기 by messageId
    @Override
    public Message readMessageById(UUID messageId) {
        // Message ID `null` 검증
        ValidationMethods.validateMessageId(messageId);

        Message message = data.get(messageId);
        ValidationMethods.existMessage(message);

        return message;
    }

    // R. 모두 읽기
    @Override
    public List<Message> readAllMessage() {

        return new ArrayList<>(data.values());
    }
}
