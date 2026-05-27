package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.MessageDTO;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import com.sprint.mission.discodeit.exception.storage.FileStorageException;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Tag(name = "메시지", description = "메시지 API")
public class MessageController {
    private final MessageService messageService;

    // 메시지 생성 - POST /api/messages (201 Created)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "메시지 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "메시지 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "channel 또는 user를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject("Channel or user not found"))
            )
    })
    public ResponseEntity<MessageDTO> postMessage(@Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
                                                  @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        log.info("메시지 생성 요청 - channelId: {}, authorId: {}", request.channelId(), request.authorId());
        List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
                .map(files -> files.stream()
                        .map(file -> {
                            try {
                                return new BinaryContentCreateRequest(
                                        file.getOriginalFilename(),
                                        file.getContentType(),
                                        file.getBytes()
                                );
                            } catch (IOException e) {
                                throw new FileStorageException(file.getOriginalFilename());
                            }
                        }).toList())
                .orElse(new ArrayList<>());
        return ResponseEntity.ok(messageService.create(request, attachmentRequests));
    }

    // 메시지 수정 - PATCH /api/messages/{messageId} (200 OK)
    @PatchMapping("/{messageId}")
    @Operation(summary = "메시지 내용 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "메시지 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 메시지를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject("Message not found"))
            )
    })
    @PreAuthorize("@messageService.isMessageOwner(#messageId, authentication.principal.userDTO.id)")
    public ResponseEntity<MessageDTO> updateMessage(
            @Parameter(
                    description = "수정할 messageId",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest request) {
        log.info("메시지 수정 요청 - messageId: {}", messageId);
        return ResponseEntity.ok(messageService.update(messageId, request));
    }

    // 메시지 삭제 - DELETE /api/messages/{messageId} (204 No Content)
    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "메시지 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "메시지 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 메시지를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject("Message not found"))
            )
    })
    @PreAuthorize("@messageService.isMessageOwner(#messageId, authentication.principal.userDTO.id)")
    public void deleteMessage(
            @Parameter(
                    description = "삭제할 messageId",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID messageId
    ) {
        log.info("메시지 삭제 요청 - messageId: {}", messageId);
        messageService.deleteMessage(messageId);
    }

    // 특정 Channel 메시지 목록 조회 - GET /api/messages?channelId=channelId (200 OK)
    @GetMapping
    @Operation(summary = "해당 channel의 모든 message 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "channel의 모든 message 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = MessageDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 channel 찾을 수 없음",
                    content = @Content(examples = @ExampleObject("Channel not found"))
            )
    })
    public PageResponse<MessageDTO> getAllMessages(@RequestParam UUID channelId,
                                                   @RequestParam(value = "cursor", required = false) Instant cursor,
                                                   Pageable pageable) {
        log.debug("채널 메시지 목록 조회 요청 - channelId: {}, cursor: {}, pageable: {}", channelId, cursor, pageable);
        return messageService.findMessagesByChannel(channelId, cursor, pageable);
    }
}
