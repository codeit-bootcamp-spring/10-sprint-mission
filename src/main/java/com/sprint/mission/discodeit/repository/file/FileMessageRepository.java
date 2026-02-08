package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class FileMessageRepository extends FileDomainRepository<Message> implements MessageRepository {

    public FileMessageRepository() throws IOException {
        super(Paths.get(System.getProperty("user.dir"), "file-data-map", "Message"),
                ".msg");
    }

    @Override
    public Message save(Message message) throws IOException {
        return save(message, Message::getId);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) throws IOException {
        return filter(message -> message.isInChannel(channelId)).toList();
    }
}
