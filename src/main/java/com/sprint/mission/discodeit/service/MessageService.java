package com.sprint.mission.discodeit.service;

<<<<<<< HEAD

=======
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
<<<<<<< HEAD
    Message createMessage(UUID userId, UUID channelId, String content);
    Message findById(UUID id);
    List<Message> findAll();
    void updateMessage(UUID id, String content, UUID userId, UUID channelId);
    void delete(UUID id);
=======
    Message createMessage(String content, UUID channelId, UUID userId);
    void deleteMessage(UUID id);
    Message findMessageById(UUID id);
    Message updateMessage(UUID id, String newContent);
    List<Message> findAllMessages();
    List<Message> findMessagesByChannelId(UUID channelId);
    List<Message> findMessagesByUserId(UUID userId);
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
}
