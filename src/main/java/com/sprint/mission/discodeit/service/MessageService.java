package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 메시지 생성
    MessageResponseDTO createMessage(MessageCreateRequestDTO messageCreateRequestDTO);

    // 메시지 단건 조회
    MessageResponseDTO searchMessage(UUID targetMessageId);

    // 특정 채널에서 발행한 전체 메시지 목록 조회
    List<MessageResponseDTO> searchMessageAllByChannelId(UUID channelId);

    // 특정 사용자가 발행한 전체 메시지 목록 조회
    List<MessageResponseDTO> searchMessagesByUserId(UUID targetUserId);

    // 메시지 수정
    MessageResponseDTO updateMessage(MessageUpdateRequestDTO messageUpdateRequestDTO);

    // 메시지 삭제
    void deleteMessage(UUID targetMessageId);
}
