package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 메시지 생성
    MessageResponseDTO create(MessageCreateRequestDTO messageCreateRequestDTO);

    // 메시지 단건 조회
    MessageResponseDTO findById(UUID targetMessageId);

    // 메시지 전체 조회
    List<MessageResponseDTO> findAll();

    // 특정 채널에서 발행한 전체 메시지 목록 조회
    List<MessageResponseDTO> findAllByChannelId(UUID channelId);

    // 특정 사용자가 발행한 전체 메시지 목록 조회
    List<MessageResponseDTO> findAllByUserId(UUID targetUserId);

    // 메시지 수정
    MessageResponseDTO update(UUID messageId, MessageUpdateRequestDTO messageUpdateRequestDTO);

    // 메시지 삭제
    void delete(UUID targetMessageId);
}
