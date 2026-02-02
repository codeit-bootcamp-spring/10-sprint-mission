package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("jcf")
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message save(Message message){
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id){
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll(){
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(Message message){
        data.remove(message.getId());
    }

    // 특정 채널의 모든 메시지 삭제
    @Override
    public void deleteByChannelId(UUID channelId){
        data.values().removeIf(message ->
                message.getChannel().getId().equals(channelId));
    }
}
