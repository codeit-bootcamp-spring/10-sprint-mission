package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageDto> createMessage(
            @RequestPart("messageCreateRequest") @Valid CreateMessageRequestDTO dto,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    ) {
        log.debug("[MESSAGE_CREATE_REQUEST] 메시지 생성 요청: authorId={}, channelId={}", dto.authorId(), dto.channelId());
        List<CreateBinaryContentPayloadDTO> payloads = List.of();

        if (attachments != null && attachments.length > 0) {
            payloads = Arrays.stream(attachments)
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> {
                        try {
                            return new CreateBinaryContentPayloadDTO(
                                    file.getBytes(),
                                    file.getContentType(),
                                    file.getOriginalFilename(),
                                    file.getSize()
                            );
                        } catch (IOException e) {
                            log.warn("[MESSAGE_CREATE_REQUEST_FAIL] 첨부 파일 읽기 실패로 메시지 생성 요청 실패: authorId={}, channelId={}, filename={}",
                                    dto.authorId(), dto.channelId(), file.getOriginalFilename());
                            throw new BinaryContentException(
                                    ErrorCode.BINARY_CONTENT_CAN_NOT_READ,
                                    Map.of("authorId", dto.authorId(), "channelId", dto.channelId(), "filename", file.getOriginalFilename())
                            );
                        }
                    })
                    .toList();
        }

        MessageDto created = messageService.createMessage(dto, payloads);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity updateMessage(
            @PathVariable UUID messageId,
            @RequestBody @Valid UpdateMessageRequestDTO dto
    ) {
        log.debug("[MESSAGE_UPDATE_REQUEST] 메시지 수정 요청: messageId={}", messageId);
        MessageDto updated = messageService.updateMessage(messageId, dto);

        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMessage(
            @PathVariable UUID messageId
            ) {
        log.debug("[MESSAGE_DELETE_REQUEST] 메시지 삭제 요청: messageId={}", messageId);
        messageService.deleteMessage(messageId);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAllByChannelId(
            @RequestParam UUID channelId,
            @RequestParam(required = false) Instant cursor,
            Pageable pageable
    ) {
        PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor, pageable);

        return ResponseEntity.ok(messages);
    }

    @RequestMapping(value = "/by-user", method = RequestMethod.GET)
    public ResponseEntity findAllByUserId(
            @RequestParam UUID userId,
            @RequestParam(required = false) Instant cursor,
            Pageable pageable
    ) {
        PageResponse<MessageDto> messages = messageService.findAllByUserId(userId, cursor, pageable);

        return ResponseEntity.ok(messages);
    }
}
