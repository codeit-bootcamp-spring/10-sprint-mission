package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.Set;
import java.util.UUID;

public interface MessageRepository {
    public void fileSave(Set<Message> messages);
    public Set<Message> fileLoadAll();
    public Message fileLoad(UUID id);
    public void fileDelete(UUID id);
}
