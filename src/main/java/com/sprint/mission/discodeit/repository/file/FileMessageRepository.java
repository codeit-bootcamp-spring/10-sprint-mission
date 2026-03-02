package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import com.sprint.mission.discodeit.utils.FileLockProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository extends FileRepositoryLockSupport implements MessageRepository {

    private static final Path MESSAGE_DIRECTORY =
            FileIOHelper.resolveDirectory("messages");

    public FileMessageRepository(FileLockProvider fileLockProvider) {
        super(fileLockProvider);
    }

    @Override
    public void save(Message message) {
        Path messageFilePath = MESSAGE_DIRECTORY.resolve(message.getId().toString());

        withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.save(messageFilePath, message));
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path messageFilePath = MESSAGE_DIRECTORY.resolve(id.toString());

        return withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.load(messageFilePath));
    }

    @Override
    public List<Message> findAll() {
        return withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.loadAll(MESSAGE_DIRECTORY));
    }

    @Override
    public void delete(Message message) {
        Path channelFilePath = MESSAGE_DIRECTORY.resolve(message.getId().toString());

        withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.delete(channelFilePath));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.<Message>loadAll(MESSAGE_DIRECTORY).stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList());
    }

    @Override
    public Instant findLastMessageAtByChannelId(UUID channelId) {
        return withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.<Message>loadAll(MESSAGE_DIRECTORY).stream()
                .filter(message ->
                        message.getChannelId().equals(channelId)
                )
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null));
    }

    @Override
    public Map<UUID, Instant> findLastMessageAtByChannelIds(List<UUID> channelIds) {
        return withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.<Message>loadAll(MESSAGE_DIRECTORY).stream()
                .filter(message -> channelIds.contains(message.getChannelId()))
                .collect(
                        java.util.stream.Collectors.toMap(
                                Message::getChannelId,
                                Message::getCreatedAt,
                                (t1, t2) -> t1.isAfter(t2) ? t1 : t2
                        )
                ));
    }

    @Override
    public void deleteById(UUID messageId) {
        Path messagePath = MESSAGE_DIRECTORY.resolve(messageId.toString());
        withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.delete(messagePath));
    }
}
