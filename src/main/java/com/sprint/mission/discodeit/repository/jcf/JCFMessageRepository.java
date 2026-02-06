package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class JCFMessageRepository extends JCFDomainRepository<Message> implements MessageRepository {

    public JCFMessageRepository() {
        super(new HashMap<>());
    }

    @Override
    public Message save(Message message) {
        getData().put(message.getId(), message);
        return message;
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return getData().values()
                .stream()
                .filter(message -> message.isInChannel(channelId))
                .toList();
    }
}
