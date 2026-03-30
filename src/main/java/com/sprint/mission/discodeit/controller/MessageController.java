package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessagePatchDto;
import com.sprint.mission.discodeit.dto.MessagePostDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message controller 입니다.")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Channel의 Message 목록 조회", operationId = "findAllByChannelId")
    public ResponseEntity<PageResponse<MessageDto>> getMessage(
        @Parameter(name = "channelId", description = "조회할 Channel ID") @RequestParam(value = "channelId") UUID channelId,
        @Parameter(name = "cursor", description = "페이징 커서 정보") @RequestParam(required = false) Instant cursor,
        @Parameter(name = "pageable", description = "페이징 정보") @RequestParam(required = false) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(messageService.findByChannelId(channelId, cursor, pageable));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Message 생성", operationId = "create_2")
    public ResponseEntity<MessageDto> createMessage(
        @Valid @RequestPart(name = "messageCreateRequest") MessagePostDto messagePostDto,
        @Parameter(description = "Message 첨부 파일들") @RequestPart(name = "attachments", required = false) List<MultipartFile> attachments) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(messageService.create(messagePostDto, attachments));
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    @Operation(summary = "Message 내용 수정", operationId = "update_2")
    public ResponseEntity<MessageDto> updateMessage(
        @Parameter(name = "messageId", description = "수정할 Message ID") @PathVariable UUID messageId,
        @Valid @RequestBody MessagePatchDto messagePatchDto) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(messageService.updateById(messageId, messagePatchDto));
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    @Operation(summary = "Message 삭제", operationId = "delete_1")
    public ResponseEntity<?> deleteMessage(
        @Parameter(name = "messageId", description = "삭제할 Message ID") @PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
