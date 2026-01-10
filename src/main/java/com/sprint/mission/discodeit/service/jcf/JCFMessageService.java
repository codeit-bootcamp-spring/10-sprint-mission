package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    public JCFMessageService() { this.data = new HashMap<>();}

    @Override
    public Message createMessage(Channel channel, User author, String content) {
        if(channel == null) throw new IllegalArgumentException("채널 정보가 없습니다");
        if(author == null) throw new IllegalArgumentException("유저 정보가 없습니다");
        if(content == null) throw new IllegalArgumentException("내용을 다시 입력해주세요");
        Message message = new Message(channel, author, content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateMessage(String content, UUID id) {
        Message message = data.get(id);
        message.update(content);
    }

    @Override
    public void deleteMessage(UUID id) {
        data.remove(id);
    }
}
