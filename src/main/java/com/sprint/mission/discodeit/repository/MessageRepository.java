package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    void save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    void delete(Message message);

    void deleteByUser(User user);

    void deleteByChannel(Channel channel);
}
