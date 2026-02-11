package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.extend.FileSerializerDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository extends FileSerializerDeserializer<Message> implements MessageRepository {
    private final String MESSAGE_DATA_DIRECTORY = "message";

    public FileMessageRepository() {
        super(Message.class);
    }

    @Override
    public Message save(Message message) {
        return super.save(MESSAGE_DATA_DIRECTORY, message);
    }

    @Override
    public Optional<Message> findById(UUID uuid) {
        return super.load(MESSAGE_DATA_DIRECTORY, uuid);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return super.loadAll(MESSAGE_DATA_DIRECTORY).stream()
                .filter(m -> Objects.equals(m.getChannelId(), channelId))
                .toList();
    }

    @Override
    public void deleteById(UUID uuid) {
        super.delete(MESSAGE_DATA_DIRECTORY, uuid);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        findAllByChannelId(channelId)
                .forEach(m -> deleteById(m.getId()));
    }
}
