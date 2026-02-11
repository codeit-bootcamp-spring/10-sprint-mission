package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private static final Path MESSAGE_DIRECTORY =
            FileIOHelper.resolveDirectory("messages");

    @Override
    public void save(Message message) {
        Path messageFilePath = MESSAGE_DIRECTORY.resolve(message.getId().toString());

        FileIOHelper.save(messageFilePath, message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path messageFilePath = MESSAGE_DIRECTORY.resolve(id.toString());

        return FileIOHelper.load(messageFilePath);
    }

    @Override
    public List<Message> findAll() {
        return FileIOHelper.loadAll(MESSAGE_DIRECTORY);
    }

    @Override
    public void delete(Message message) {
        Path channelFilePath = MESSAGE_DIRECTORY.resolve(message.getId().toString());

        FileIOHelper.delete(channelFilePath);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return FileIOHelper.<Message>loadAll(MESSAGE_DIRECTORY).stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public Instant findLastMessageAtByChannelId(UUID channelId) {
        return FileIOHelper.<Message>loadAll(MESSAGE_DIRECTORY).stream()
                .filter(message ->
                        message.getChannelId().equals(channelId)
                )
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }

    @Override
    public Map<UUID, Instant> findLastMessageAtByChannelIds(List<UUID> channelIds) {
        return FileIOHelper.<Message>loadAll(MESSAGE_DIRECTORY).stream()
                .filter(message -> channelIds.contains(message.getChannelId()))
                .collect(
                        java.util.stream.Collectors.toMap(
                                Message::getChannelId,
                                Message::getCreatedAt,
                                (t1, t2) -> t1.isAfter(t2) ? t1 : t2
                        )
                );
    }

    @Override
    public void deleteById(UUID messageId) {
        Path messagePath = MESSAGE_DIRECTORY.resolve(messageId.toString());
        FileIOHelper.delete(messagePath);
    }
}
