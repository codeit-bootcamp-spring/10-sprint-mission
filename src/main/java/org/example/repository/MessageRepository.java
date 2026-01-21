package org.example.repository;

import org.example.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    Message save(Message message);

    Optional<Message> findById(UUID messageId);

    List<Message> findAll();

    void deleteById(UUID messageId);

    boolean existsById(UUID messageId);
}
