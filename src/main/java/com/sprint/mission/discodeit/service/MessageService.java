package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 메시지 생성
    MessageResponseDTO createMessage(MessageCreateRequestDTO messageCreateRequestDTO);

    // 메시지 단건 조회
    Message searchMessage(UUID targetMessageId);

    // 메시지 전체 조회
    List<Message> searchMessageAll();

    // 메시지 수정
    Message updateMessage(UUID targetMessageId, String newMessage);

    // 메시지 저장
    void updateMessage(UUID channelId, Channel channel);

    // 메시지 삭제
    void deleteMessage(UUID targetMessageId);
}
