package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
public class JCFMessageRepository extends JCFDomainRepository<Message> implements MessageRepository {

    public JCFMessageRepository() {
        super(new HashMap<>());
    }

    @Override
    public Message save(Message message) {
        getData().put(message.getId(), message);
        return message;
    }

}
