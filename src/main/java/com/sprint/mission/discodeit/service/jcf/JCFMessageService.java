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
        if (!messageChannel.getChannelUsersList().contains(author)) {
            throw new IllegalArgumentException("author는 해당 channel에 참가하지 않았습니다.");
        }

        // 연관
        // channel 객체에 추가
        messageChannel.addMessageInChannel(message);

        // author(user) 객체에 추가
        author.addMessageInUser(message);

        data.put(message.getId(), message);
        return message;
    }

    // R. 읽기
    // 특정 메시지 정보 읽기 by messageId
    @Override
    public Optional<Message> readMessageById(UUID messageId) {
        // Message ID `null` 검증
        ValidationMethods.validateMessageId(messageId);

        Message message = data.get(messageId);

        return Optional.ofNullable(message);
    }

    // R. 모두 읽기
    // 메시지 전체
    @Override
    public List<Message> readAllMessage() {
        return new ArrayList<>(data.values());
    }

    // U. 수정
    public static Message validationMethods(Map<UUID, Message> data, UUID requestId, UUID messageId) {
        // request(수정하려는 user) ID `null` 검증
        ValidationMethods.validateUserId(requestId);
        // Message ID `null` 검증
        ValidationMethods.validateMessageId(messageId);
        // request(수정하려는 user) ID와 message 작성자(user) ID가 동일한지 확인
        ValidationMethods.validateSameId(requestId, data.get(messageId).getAuthor().getId());

        Message message = data.get(messageId);
        ValidationMethods.existMessage(message);

        return message;
    }
    // 메시지 수정
    @Override
    public Message updateMessageContent(UUID requestId, UUID messageId, String content) {
        // id null 검증 / request ID와 message 작성자(user) ID가 동일한지 확인 / message 객체 존재 확인
        Message message = validationMethods(data, requestId, messageId);
        // content `null` or `blank` 검증
        ValidationMethods.validateString(content, "content");

        message.updateContent(content);
        return message;
    }

    // D. 삭제
    @Override
    public void deleteMessage(UUID requestId, UUID messageId) {
        // id null 검증 / request ID와 message 작성자(user) ID가 동일한지 확인 / message 객체 존재 확인
        Message message = validationMethods(data, requestId, messageId);

        // 연관 관계에 따른 다른 객체 리스트에서 삭제할 메세지 삭제
        // user(author) 객체
        message.getAuthor().removeMessageInUser(message);
        // channel(messageChannel) 객체
        message.getMessageChannel().removeMessageInChannel(message);

        data.remove(messageId);
    }
}
