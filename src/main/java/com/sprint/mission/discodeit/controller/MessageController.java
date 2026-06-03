package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;
import com.sprint.mission.discodeit.dto.response.message.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "Message 생성", operationId = "create_2")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(@Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
                                             @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        MessageDto response = messageService.create(messageCreateRequest, attachments);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Channel의 Message 목록 조회", operationId = "findAllByChannelId")
    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId (@RequestParam UUID channelId,
                                                                        @RequestParam(required = false) Instant cursor,
                                                                        @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<MessageDto> response = messageService.findAllByChannelId(channelId, cursor, pageable.getPageSize());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Message 내용 수정", operationId = "update_2")
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageDto> update(@PathVariable UUID messageId,
                                             @Valid @RequestBody MessageUpdateRequest messageUpdateRequest) {
        MessageDto response = messageService.update(messageId, messageUpdateRequest);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Message 삭제", operationId = "delete_1")
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);

        return ResponseEntity.noContent().build();
    }
}
