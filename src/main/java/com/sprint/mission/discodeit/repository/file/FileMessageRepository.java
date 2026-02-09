package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Paths;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileMessageRepository extends FileDomainRepository<Message> implements MessageRepository {

    public FileMessageRepository() throws IOException {
        super(Paths.get(System.getProperty("user.dir"), "file-data-map", "Message"),
                ".msg");
    }

    @Override
    public Message save(Message message) throws IOException {
        return save(message, Message::getId);
    }

}
