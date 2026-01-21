package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageMap = new HashMap<>();

    @Override
    public Message save(Message message) {
        messageMap.put(message.getId(), message);
        return message;
    }

    @Override
    public void delete(UUID id){
        messageMap.remove(id);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(messageMap.get(id));
    }

    @Override
    public List<Message> findAll(){
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId){
        return messageMap.values().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                        .toList();
    }

    public List<Message> findByUserId(UUID userId){
        return messageMap.values().stream()
                .filter(message -> message.getUser().getId().equals(userId))
                .toList();
    }
}
