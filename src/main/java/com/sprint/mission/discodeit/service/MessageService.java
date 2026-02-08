package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
//    Message create(String content, UUID channelId, UUID authorId);
//    Message find(UUID Id);
//    List<Message> findAll();
//    Message update(UUID messageId, String newContent);

    MessageResponse create(CreateMessageRequest request);        // DTO 사용
    MessageResponse find(UUID messageId);                        // DTO 반환
    List<MessageResponse> findAllByChannelId(UUID channelId);   // 메서드명 변경 + DTO 반환
    MessageResponse update(UpdateMessageRequest request);        // DTO 사용
    void delete(UUID messageId);


}
