package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.utils.Validation;
import com.sprint.mission.discodeit.repository.MessageRepository;

import javax.swing.*;
import java.util.*;
public class BasicMessageService implements MessageService{
    private final MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

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
        return messageRepository.findById(id);
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
        Message msg = messageRepository.findById(uuid);
        msg.update(newContent);
        messageRepository.save(msg);
        return msg;
    }
    @Override
    public void deleteMessage(UUID id){
        messageRepository.delete(id);
    }


}
