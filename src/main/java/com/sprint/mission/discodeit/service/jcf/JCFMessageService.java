package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    final ArrayList<Message> list;

    public JCFMessageService() {
        this.list = new ArrayList<>();
    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        validationMessage(content);
        Objects.requireNonNull(channelId, "channelId는 null이 될 수 없습니다.");
        Objects.requireNonNull(userId, "userId는 null이 될 수 없습니다.");
        Message message = new Message(content, channelId, userId);
        list.add(message);
        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        for (Message message : list) {
            if (id.equals(message.getId())){
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> readAllMessage() {
        return list;
    }

    @Override
    public void updateMessage(UUID id, String content) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        validationMessage(content);
        readMessage(id).updateContent(content);
    }

    public void deleteMessage(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        list.remove(readMessage(id));
    }

    public boolean isMessageDeleted(UUID id) {
        Objects.requireNonNull(id, "id는 null이 될 수 없습니다.");
        for (Message message : list) {
            if(id.equals(message.getId())) {
                return false;
            }
        }
        return true;
    }

    private void validationMessage(String content) {
        Objects.requireNonNull(content, "content는 null이 될 수 없습니다.");
        if(content.isBlank()) {
            throw new IllegalArgumentException("content에 공백을 입력할 수 없습니다.");
        }
    }

}
