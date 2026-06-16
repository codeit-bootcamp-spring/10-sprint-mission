package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.facade.MessageFacade;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 메시지 관련 요청을 처리하는 컨트롤러 클래스입니다.
 * 메시지 생성 시 첨부 파일 처리 및 메시지 조회/수정/삭제를 담당합니다.
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController implements MessageApi {
    private final MessageService messageService;
    private final MessageFacade messageFacade;

    @Override
    @Timed("message.create.async")
    public ResponseEntity<MessageDto.Response> createMessage(MessageDto.CreateRequest request, List<MultipartFile> files) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageFacade.createMessage(request, files));
    }

    @Override
    public ResponseEntity<MessageDto.Response> updateMessage(UUID messageId, MessageDto.UpdateRequest request) {
        return ResponseEntity.ok(messageService.update(messageId, request));
    }

    @Override
    public ResponseEntity<MessageDto.Response> findMessage(UUID messageId) {
        return ResponseEntity.ok(messageService.find(messageId));
    }

    @Override
    public ResponseEntity<Void> deleteMessage(UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PageResponse<MessageDto.Response>> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId, cursor, pageable));
    }
}
