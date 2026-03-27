package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/messages")
@Validated
@Tag(name = "Message")
@Slf4j
public class MessageController {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Message 생성")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음")
  })
  public ResponseEntity<MessageDto> create(
      @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    log.info("메시지 생성 요청: authorId={}, channelId={}", request.authorId(),
        request.channelId());
    MessageDto response = messageService.create(request, attachments);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{messageId}")
  @Operation(summary = "Message 내용 수정")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
  })
  public ResponseEntity<MessageDto> update(
      @Parameter(description = "수정할 Message ID", example = "4e23cb1a-e2ae-4171-811f-ccc4c13fc577")
      @NotNull @PathVariable UUID messageId,
      @Valid @RequestBody MessageUpdateRequest request
  ) {
    log.info("메시지 수정 요청: messageId={}", messageId);
    MessageDto response = messageService.update(messageId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{messageId}")
  @Operation(summary = "Message 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
  })
  public ResponseEntity<MessageDto> delete(
      @Parameter(description = "삭제할 Message ID", example = "4e23cb1a-e2ae-4171-811f-ccc4c13fc577")
      @NotNull @PathVariable UUID messageId
  ) {
    log.info("메시지 삭제 요청: messageId={}", messageId);
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @Parameter(description = "조회할 Channel ID", example = "0ad06ce5-bdfb-4304-b6eb-a133a4b49fb8")
      @NotNull @RequestParam("channelId") UUID channelId,
      @Parameter(description = "페이징 커서 정보")
      @RequestParam(value = "cursor", required = false) Instant cursor,
      Pageable pageable
  ) {
    log.debug("메시지 목록 조회 요청: channelId={}", channelId);
    PageResponse<MessageDto> response = messageService.findAllByChannelId(channelId, cursor,
        pageable);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
