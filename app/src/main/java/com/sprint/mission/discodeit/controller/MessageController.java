package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Message")
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  // 메시지 생성
  @Operation(summary = "Message 생성")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @Timed("messageDto.create.async")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> createMessage(
      @Valid @RequestPart("messageCreateRequest") MessageDto.MessageCreateRequest messageReq,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments)
      throws IOException {
    boolean hasAttachment = attachments != null && !attachments.isEmpty();

    log.info("[Controller] 메세지 생성 요청: authorId={}, channelId={}, hasAttachment={}",
        messageReq.authorId(), messageReq.channelId(), hasAttachment);

    MessageDto dto = messageService.createMessage(messageReq, attachments);
    log.debug("[Controller] 메세지 생성 응답 준비: id={}", dto.id());

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // 메시지 채널 조회(UUID)
  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
  @GetMapping(params = "channelId")
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(@RequestParam UUID channelId,
      @RequestParam(required = false) Instant cursor,
      @PageableDefault(size = 50) Pageable pageable) {
    PageResponse<MessageDto> dto = messageService.findAllByChannelId(channelId, cursor, pageable);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  // 메시지 수정
  @Operation(summary = "Message 내용 수정")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PatchMapping("/{message-id}")
  public ResponseEntity<MessageDto> updateMessage(@PathVariable("message-id") UUID messageId,
      @RequestBody MessageDto.MessageUpdateRequest messageReq) {
    log.info("[Controller] 메세지 수정 요청: id={}", messageId);

    MessageDto dto = messageService.updateMessage(messageId, messageReq);
    log.debug("[Controller] 메세지 수정 응답 준비: id={}", dto.id());

    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  // 메시지 삭제
  @Operation(summary = "Message 내용 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{message-id}")
  public ResponseEntity<Void> deleteMessage(@PathVariable("message-id") UUID messageId)
      throws IOException {
    log.info("[Controller] 메세지 삭제 요청: id={}", messageId);

    messageService.deleteMessage(messageId);
    log.debug("[Controller] 메세지 삭제 응답 준비: id={}", messageId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
