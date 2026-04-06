package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Message")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;
  private final PageResponseMapper pageResponseMapper;

  @Operation(summary = "Message 생성", operationId = "create_2")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = MessageDto.class))),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found")))
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> createMessage(
      @Parameter(description = "Message 생성 정보", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @RequestPart("messageCreateRequest") @Valid MessageCreateRequest request,
      @Parameter(description = "Message 첨부 파일들")
      @RequestPart(value = "attachments", required = false) List<MultipartFile> files) {
    log.info("REST request to create Message: channelId={}, authorId={}", request.getChannelId(),
        request.getAuthorId());
    return ResponseEntity.status(HttpStatus.CREATED).body(messageService.createMessage(request, files));
  }

  @Operation(summary = "Channel의 Message 목록 조회", operationId = "findAllByChannelId")
  @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공",
      content = @Content(schema = @Schema(implementation = MessageDto.class)))
  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> getMessages(
      @Parameter(description = "조회할 Channel ID", required = true)
      @RequestParam UUID channelId,
      @Parameter(description = "마지막으로 본 메시지의 생성 시간 (커서)")
      @RequestParam(required = false) Instant cursor,
      @Parameter(description = "페이지 크기")
      @RequestParam(defaultValue = "50") int size) {
    log.debug("REST request to get Messages: channelId={}, cursor={}, size={}", channelId, cursor,
        size);
    Slice<MessageDto> messageSlice = messageService.findAllByChannelId(channelId, cursor, size);
    return ResponseEntity.ok(pageResponseMapper.fromSlice(messageSlice, MessageDto::createdAt));
  }

  @Operation(summary = "Message 내용 수정", operationId = "update_2")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = MessageDto.class))),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
  })
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> updateMessage(
      @Parameter(description = "수정할 Message ID", required = true)
      @PathVariable UUID messageId,
      @RequestBody @Valid MessageUpdateRequest request) {
    log.info("REST request to update Message: id={}", messageId);
    return ResponseEntity.ok(messageService.updateMessage(messageId, request));
  }

  @Operation(summary = "Message 삭제", operationId = "delete_1")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
  })
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(
      @Parameter(description = "삭제할 Message ID", required = true)
      @PathVariable UUID messageId) {
    log.info("REST request to delete Message: id={}", messageId);
    messageService.deleteMessage(messageId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "/all")
  public ResponseEntity<List<MessageDto>> getAllMessages() {
    log.debug("REST request to get all Messages");
    return ResponseEntity.ok(messageService.getAllMessages());
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<MessageDto> getMessage(@PathVariable UUID id) {
    log.debug("REST request to get Message: id={}", id);
    return ResponseEntity.ok(messageService.getMessage(id));
  }

  @GetMapping(value = "/user/{userId}")
  public ResponseEntity<List<MessageDto>> getMessagesByUser(@PathVariable UUID userId) {
    log.debug("REST request to get Messages by user: id={}", userId);
    return ResponseEntity.ok(messageService.getMessagesByUserId(userId));
  }
}
