package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;
import java.util.stream.IntStream;

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
}
