package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: messageID과 내용 출력
    MessageDto create(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachments);

    // R. 읽기
    // 특정 메시지 정보 읽기
    MessageDto find(UUID messageId);

    // R. 모두 읽기
    // 메시지 전체
    List<MessageDto> findAll();
    // 특정 채널의 모든 메시지 읽어오기
    PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

    // U. 수정
    // 메시지 수정
    MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest);

    // D. 삭제
    void delete(UUID messageId);
}
