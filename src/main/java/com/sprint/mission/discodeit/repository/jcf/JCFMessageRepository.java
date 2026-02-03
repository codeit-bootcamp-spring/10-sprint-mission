package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
public class JCFMessageRepository implements MessageRepository {
    final ArrayList<Message> data;

    public JCFMessageRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message save(Message message) {
        OptionalInt indexOpt = IntStream.range(0, this.data.size())
                .filter(i -> data.get(i).getId().equals(message.getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            data.set(indexOpt.getAsInt(), message);
        } else {
            data.add(message);
        }

        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }


    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    } // 저장 로직


    @Override
    public void deleteById(UUID id) {
        data.removeIf(message -> message.getId().equals(id));
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        data.removeIf(message -> message.getChannelId().equals(channelId));
    }

    @Override
    public Optional<Instant> findLatestCreatedAtByChannelId(UUID channelId) {
        return data.stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder());
    }

}

