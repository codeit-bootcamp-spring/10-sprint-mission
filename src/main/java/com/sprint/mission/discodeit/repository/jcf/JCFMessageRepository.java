package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final HashMap<UUID, Message> data = new HashMap<>();

    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public void delete(Message message) {
        data.remove(message.getId());
    }

    public void deleteByUser(User user) {
        List<Message> targets = data.values().stream()
                .filter(m -> m.getSender().equals(user))
                .toList();

        targets.forEach(target -> delete(target));
    }

    public void deleteByChannel(Channel channel) {
        List<Message> targets = data.values().stream()
                .filter(m -> m.getChannel().equals(channel))
                .toList();

        targets.forEach(target -> delete(target));
    }
}
