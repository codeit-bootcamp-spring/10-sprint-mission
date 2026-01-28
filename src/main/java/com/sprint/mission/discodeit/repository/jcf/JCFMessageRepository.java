package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageStore;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageRepository(UserService userService, ChannelService channelService) {
        this.messageStore = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message save (Message message){
        messageStore.put(message.getId(),message);
        return message;
    }

    @Override
    public Message findById (UUID id){
        Message message = messageStore.get(id);
        if(message == null){
            throw new IllegalArgumentException("해당 메세지를 찾을 수 없습니다");
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageStore.values());
    }

    @Override
    public void deleteById(UUID messageId) {
        messageStore.remove(messageId);
    }

    @Override
    public List<Message> findByUserId(UUID userId) {
        return messageStore.values().stream()
                .filter(message -> message.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messageStore.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }
}
