package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private final List<Message> messages = new ArrayList<>();

    // 메시지 생성
    public Message createMessage(Message message) {
        messages.add(message);
        return message;
    }

    // 단건 조회
    @Override
    public Message findMessage(UUID messageId) {
        return messages.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .orElseThrow(() -> new MessageNotFoundException("해당 메시지가 존재하지 않습니다."));
    }

    // 채널별 메시지 조회
    public List<Message> findAllByChannelMessage(Channel channel) {
        return messages.stream()
                .filter(m -> m.getChannel().equals(channel))
                .collect(Collectors.toList());
    }

    // 전체 메시지 조회
    public List<Message> findAllMessage() {
        return new ArrayList<>(messages);
    }

    // 메시지 수정
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = findMessage(messageId);
        message.updateContent(newContent);
        return message;
    }

    // 메시지 삭제
    public void deleteMessage(UUID messageId) {
        Message message = findMessage(messageId);
        messages.remove(message);
    }
}
