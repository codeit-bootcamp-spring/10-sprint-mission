package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.*;

public interface MessageRepository {
    void save(Message message);
    void delete(UUID id);
    Optional<Message> findById(UUID id);
    List<Message> findAll();
}
