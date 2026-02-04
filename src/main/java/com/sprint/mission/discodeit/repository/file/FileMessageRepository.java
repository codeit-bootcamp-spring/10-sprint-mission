package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository extends AbstractFileRepository<Message> implements MessageRepository {

    public FileMessageRepository(@Value("${discodeit.repository.file-directory:.discodeit}")String directoryPath) {
        super(directoryPath + File.separator + "Message.ser");
    }

    @Override
    public List<Message> findAllByUserId(UUID userId) {
        Map<UUID, Message> data = load();
        return data.values().stream()
                .filter(m -> m.getSenderId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        Map<UUID, Message> data = load();
        return data.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        Map<UUID, Message> data = load();
        data.values().removeIf(m -> m.getChannelId().equals(channelId));
        writeToFile(data);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, Message> data = load();
        if(data.values().removeIf(m -> m.getSenderId().equals(userId))) {
            writeToFile(data);
        }
    }
}
