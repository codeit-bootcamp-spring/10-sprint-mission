package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final HashMap<UUID, Message> messages = new HashMap<>();

    @Override
    public void save(Message message) {
        messages.put(message.getId(), message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(messages.values());
    }

    @Override
    public void delete(Message message) {
        messages.remove(message.getId());
    }

    public void deleteByUser(User user) {
        List<Message> targets = messages.values().stream()
                .filter(m -> m.getSender().equals(user))
                .toList();

        targets.forEach(target -> delete(target));
    }

    public void deleteByChannel(Channel channel) {
        List<Message> targets = messages.values().stream()
                .filter(m -> m.getChannel().equals(channel))
                .toList();

        targets.forEach(target -> delete(target));
    }
}
