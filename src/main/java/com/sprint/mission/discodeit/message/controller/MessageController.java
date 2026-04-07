package com.sprint.mission.discodeit.message.controller;


import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.message.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.MessageDto;
import com.sprint.mission.discodeit.message.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.service.MessageService;
import com.sprint.mission.discodeit.paging.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Message")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

  private final MessageService messageService;

  @Operation(summary = "Message 생성",
      operationId = "create_2")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201",
          description = "Message가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found")
          ))
  }
  )
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> createMessage(
      @RequestPart
      @Valid @Parameter(description = "Message 생성 정보")
      MessageCreateRequest messageCreateRequest,

      @Parameter(description = "Message 첨부 파일들")
      @RequestPart(value = "attachments", required = false)
      List<MultipartFile> attachments) {

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
                throw new RuntimeException(e);
              }
            })
            .toList())
        .orElse(new ArrayList<>());

    MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  @Operation(summary = "Message 내용 수정",
      operationId = "update_2")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Message가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(value = "Message with id {messageId} not found")
          )
      )
  })

  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> updateMessage(
      @Parameter(description = "수정할 Message ID")
      @PathVariable UUID messageId,
      @Parameter(description = "수정할 Message 내용")
      @Valid @RequestBody MessageUpdateRequest request) {

    MessageDto updatedMessage = messageService.update(messageId, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  @Operation(summary = "Message 삭제",
      operationId = "delete_1")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204",
          description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(value = "Message with id {messageId} not found")
          )
      )}
  )
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(
      @Parameter(description = "삭제할 Message ID")
      @PathVariable UUID messageId) {

    messageService.delete(messageId);

    return ResponseEntity.
        status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Message 목록 조회 성공"
      )
  )
  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @Parameter(description = "조회할 Channel ID") @RequestParam UUID channelId,
      @RequestParam(required = false) Instant cursor,
      @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor,
        pageable);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }

  @GetMapping("/{messageId}")
  public ResponseEntity<MessageDto> findById(@PathVariable UUID messageId) {
    MessageDto message = messageService.find(messageId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(message);
  }
}
