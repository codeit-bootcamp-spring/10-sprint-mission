package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message send(UUID senderId, UUID channelId, String content);
    Message updateMessage(UUID senderId, UUID Id, String content);
    List<Message> getMessagesByChannelIdAndMemberId(UUID channelId, UUID memberId);
    void delete(UUID id, UUID senderId);

}
