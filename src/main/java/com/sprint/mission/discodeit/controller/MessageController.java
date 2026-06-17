package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.binarycontent.FileIOException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping(value = "/api/messages")
public class MessageController {

  private final MessageService messageService;
  private final BinaryContentService binaryContentService;

  public MessageController(
      MessageService messageService,
      BinaryContentService binaryContentService
  ) {
    this.messageService = messageService;
    this.binaryContentService = binaryContentService;
  }

  @Operation(
      summary = "Channel의 Message 목록 조회",
      operationId = "findAllByChannelId",
      tags = {"Message"}
  )
  @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @Parameter(description = "조회할 Channel ID")
      @RequestParam UUID channelId,

      @Parameter(description = "페이징 커서 정보")
      @RequestParam(required = false) Instant cursor,

      @Parameter(description = "조회 크기", example = "50")
      @RequestParam(defaultValue = "50") int size
  ) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId, cursor, size));
  }

  @Operation(
      summary = "Message 생성",
      operationId = "create_2",
      tags = {"Message"}
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
      @ApiResponse(
          responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(
                  value = "Channel | Author with id {channelId | author} not found"
              )
          )
      )
  })
  @Timed("message.create.async")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @Parameter(description = "Message 첨부 파일들")
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<UUID> attachmentIds = uploadAttachments(attachments);

    MessageCreateRequest newRequest = new MessageCreateRequest(
        request.channelId(),
        request.authorId(),
        request.content(),
        attachmentIds
    );

    MessageDto created = messageService.create(newRequest);
    log.info("메시지가 성공적으로 생성되었습니다. id = {}", created.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @Operation(
      summary = "Message 내용 수정",
      operationId = "update_2",
      tags = {"Message"}
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(
                  value = "Message with id {messageId} not found"
              )
          )
      )
  })
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> update(
      @Parameter(description = "수정할 Message ID")
      @PathVariable UUID messageId,

      @Valid @RequestBody MessageUpdateRequest body
  ) {
    MessageUpdateRequest req = new MessageUpdateRequest(messageId, body.newContent());
    MessageDto updated = messageService.update(req);

    log.info("메시지가 정상적으로 수정되었습니다. id = {} ", messageId);
    return ResponseEntity.ok(updated);
  }

  @Operation(
      summary = "Message 삭제",
      operationId = "delete_1",
      tags = {"Message"}
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(
                  value = "Message with id {messageId} not found"
              )
          )
      )
  })
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Message ID")
      @PathVariable UUID messageId
  ) {
    messageService.delete(messageId);
    log.info("메시지가 정상적으로 삭제되었습니다. id = {}", messageId);
    return ResponseEntity.noContent().build();
  }

  private List<UUID> uploadAttachments(List<MultipartFile> attachments) {
    if (attachments == null || attachments.isEmpty()) {
      return List.of();
    }

    List<UUID> ids = new ArrayList<>();
    for (MultipartFile file : attachments) {
      if (file == null || file.isEmpty()) {
        continue;
      }

      try {
        UUID id = binaryContentService.create(
            new BinaryContentCreateRequest(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes(),
                null,
                null
            )
        );
        ids.add(id);
      } catch (IOException e) {
        throw new FileIOException();
      }
    }
    return List.copyOf(ids);
  }
}
