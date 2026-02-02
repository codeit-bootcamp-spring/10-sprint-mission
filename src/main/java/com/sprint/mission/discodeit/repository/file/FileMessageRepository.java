package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
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
    public void deleteByUser(User user) {
        List<Message> messages = FileIOHelper.loadAll(MESSAGE_DIRECTORY);

        messages.stream()
                .filter(message -> message.getSenderId().equals(user.getId()))
                .forEach(message -> {
                    Path messagePath =
                            MESSAGE_DIRECTORY.resolve(message.getId().toString());

                    FileIOHelper.delete(messagePath);
                });
    }

    @Override
    public void deleteByChannel(Channel channel) {
        List<Message> messages = FileIOHelper.loadAll(MESSAGE_DIRECTORY);

        messages.stream()
                .filter(message -> message.getChannelId().equals(channel.getId()))
                .forEach(message -> {
                    Path messagePath =
                            MESSAGE_DIRECTORY.resolve(message.getId().toString());

                    FileIOHelper.delete(messagePath);
                });
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
        return Map.of();
    }

    @Override
    public void deleteById(UUID messageId) {
        Path messagePath = MESSAGE_DIRECTORY.resolve(messageId.toString());
        FileIOHelper.delete(messagePath);
    }
}
