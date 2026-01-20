package com.sprint.mission.repository.file;

import com.sprint.mission.entity.Message;
import com.sprint.mission.repository.MessageRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageRepository extends BaseFileRepository implements MessageRepository {
    public FileMessageRepository(Path directory) {
        super(directory);
    }

    @Override
    public Message save(Message message) {
        super.save(getFilePath(message.getId()), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(load(Message::getId).get(id));
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return load(Message::getId).values().stream()
                .filter(message ->
                        message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return load(Message::getId).values().stream()
                .filter(message ->
                        message.getUser().getId().equals(userId) &&
                                message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(load(Message::getId).values());
    }

    @Override
    public void deleteById(UUID messageId) {
        super.delete(getFilePath(messageId));
    }

    private Path getFilePath(UUID messageId) {
        return super.directory.resolve(messageId.toString().concat(".ser"));
    }
}
