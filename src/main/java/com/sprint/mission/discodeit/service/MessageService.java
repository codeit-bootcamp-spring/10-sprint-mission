package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;
import com.sprint.mission.discodeit.dto.response.message.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 메시지 생성
    MessageDto create(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachments);

    // 메시지 단건 조회
    MessageDto findById(UUID messageId);

    // 메시지 전체 조회
    List<MessageDto> findAll();

    // 특정 채널에서 발행한 전체 메시지 목록 조회
    PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size);

    // 특정 사용자가 발행한 전체 메시지 목록 조회
    List<MessageDto> findAllByUserId(UUID userId);

    // 메시지 수정
    MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest);

    // 메시지 삭제
    void delete(UUID messageId);
}
