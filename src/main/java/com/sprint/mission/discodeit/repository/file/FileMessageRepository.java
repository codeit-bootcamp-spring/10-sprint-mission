package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.extend.FileSerDe;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileMessageRepository extends FileSerDe<Message> implements MessageRepository {
    private final String MESSAGE_DATA_DIRECTORY = "data/message";

    public FileMessageRepository() {
        super(Message.class);
    }

    @Override
    public Message save(Message message) {
        return super.save(MESSAGE_DATA_DIRECTORY, message);
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return super.load(MESSAGE_DATA_DIRECTORY, messageId);
    }

    @Override
    public List<Message> findAll() {
        return super.loadAll(MESSAGE_DATA_DIRECTORY);
    }

    @Override
    public void deleteById(UUID messageId) {
        super.delete(MESSAGE_DATA_DIRECTORY, messageId);
    }
}
