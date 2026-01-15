package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message sendDirectMessage(UUID senderId, UUID receiverId, String content);
    List<Message> findDirectMessagesBySenderIdAndReceiverId(UUID senderId, UUID receiverId);
    Message send(UUID senderId, UUID channelId, String content);
    Message updateMessage(UUID senderId, UUID Id, String content);
    List<Message> findMessagesByChannelIdAndMemberId(UUID channelId, UUID memberId);
    void deleteByIdAndSenderId(UUID id, UUID senderId);

}
