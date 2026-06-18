package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "2. 채팅 관리", description = "채널 내 메시지 전송, 수정, 삭제 및 조회 API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @Timed("message.create.async")
  @Operation(summary = "메시지 생성 및 파일 업로드", description = "특정 채널에 메시지를 작성하고 여러 개의 파일을 함께 업로드할 수 있습니다.")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public MessageDto create(
      @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @Parameter(description = "첨부할 파일 리스트 (이미지, 문서 등)", example = "image.png")
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

    log.info("[REQUEST] Create Message: authorId={}, channelId={}, attachments={}"
        , request.getAuthorId(), request.getChannelId(), attachments);

    return messageService.create(request, attachments);
  }

  @Operation(summary = "메시지 수정", description = "이미 보낸 메시지의 본문 내용을 수정합니다.")
  @PatchMapping("/{messageId}")
  public MessageDto update(
      @Parameter(description = "수정할 메시지의 UUID", required = true)
      @PathVariable UUID messageId,
      @Valid @RequestBody MessageUpdateRequest request) {

    log.info("[REQUEST] Update Message: id={}", messageId);

    return messageService.update(messageId, request);
  }

  @Operation(summary = "메시지 삭제", description = "특정 메시지를 시스템에서 영구적으로 삭제합니다.")
  @DeleteMapping("/{messageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @Parameter(description = "삭제할 메시지의 UUID", required = true)
      @PathVariable UUID messageId) {

    log.info("[REQUEST] Delete Message: id={}", messageId);

    messageService.delete(messageId);
  }

  @Operation(summary = "특정 채널 메시지 목록 조회", description = "커서 기반으로 특정 채널의 모든 메시지 내역을 조회합니다.")
  @GetMapping
  public PageResponse<MessageDto> findAllByChannelId(
      @Parameter(description = "조회할 채널의 UUID", required = true)
      @RequestParam UUID channelId,
      @Parameter(description = "다음 페이지 조회를 위한 커서 (마지막 메시지의 시간)")
      @RequestParam(required = false) Instant cursor,
      @Parameter(description = "조회할 메시지 개수")
      @RequestParam(defaultValue = "50") int size) {
    return messageService.findAllByChannelId(channelId, cursor, size);
  }
}
