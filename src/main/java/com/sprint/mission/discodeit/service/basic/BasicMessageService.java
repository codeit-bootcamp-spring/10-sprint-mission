package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.utils.Validation;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService{
    private final MessageRepository messageRepository;

    @Override
    public Message createMessage(String content, UUID senderId, UUID channelId) {
        Validation.notBlank(content, "메세지 내용");
        if (senderId == null || channelId == null) {
            throw new IllegalArgumentException("senderId나 channelId가 null일 수 없습니다.");
        }

        Message message = new Message(content, senderId, channelId);
        messageRepository.save(message);
        return message;
    }
    @Override
    public List<Message> getMessageAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message getMessageById(UUID id){
        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 메세지가 존재하지 않습니다: " + id));
    }

    @Override
    public List<Message> getMsgListSenderId(UUID senderId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getSender() != null
                        && m.getSender().getId().equals(senderId))
                .toList();
    }

    @Override
    public Message updateMessage(UUID uuid, String newContent) {
        Validation.notBlank(newContent, "메세지 내용");
        Message msg = messageRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("수정할 메세지가 존재하지 않습니다: " + uuid));

        msg.update(newContent);
        messageRepository.save(msg);
        return msg;
    }
    @Override
    public void deleteMessage(UUID id){
        // 없는 ID면 예외
        messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 메세지가 존재하지 않습니다: " + id));
        messageRepository.delete(id);
    }


}
