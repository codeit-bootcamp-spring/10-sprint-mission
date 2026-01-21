package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final FileStorage<Message> data;
    public FileMessageRepository() {
        this.data = new FileStorage<>("messages");
    }
    @Override
    public Message save(Message message) {
        return data.put(message.getId(), message);
    }

    @Override
    public void saveAll() {
        data.saveAll();
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAllByChannelIdOrderByCreatedAtAsc(UUID channelId) {
        return data.values().stream()
                .filter(m->m.getChannel().getId().equals(channelId))
                .sorted(Comparator
                        .comparing(Message::getCreatedAt)//시간순
                        .thenComparing(m->m.getSequence())//같은 시간이면 시퀀스로 정렬
                )
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        for(Message message: new ArrayList<>(data.values())) {
            if(message.getChannel().getId().equals(channelId)) {
                data.remove(message.getId());
            }
        }
    }
}
