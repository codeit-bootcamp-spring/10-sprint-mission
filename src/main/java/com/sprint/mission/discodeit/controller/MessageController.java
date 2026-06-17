package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.MessageService;
import io.micrometer.core.annotation.Timed;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController implements MessageApi {

  private final MessageService messageService;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  // @Timed("message.create.async") // 동기/비동기 처리 간 응답 속도 테스트를 위해 메서드 실행 시간 측정
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    log.info(
        "Received POST /api/messages request - channelId: {}, authorId: {}, attachments count: {}",
        request.channelId(), userDetails.getId(),
        attachments != null ? attachments.size() : 0); // 메시지 생성 요청 로그

    Message message = messageService.create(
        request.content(),
        userDetails.getId(),
        request.channelId(),
        attachments
    );

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(messageMapper.toDto(message));
  }

  @Override
  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam UUID channelId,
      @RequestParam(required = false) Instant cursor,
      @PageableDefault(page = 0, size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    log.info("Received GET /api/messages request - channelId: {}, cursor: {}", channelId,
        cursor); // 특정 채널의 메시지 조회 요청 로그

    // 페이징 처리된 객체 받아옴
    Slice<Message> messageSlice = messageService.findAllByChannelId(channelId, cursor, pageable);

    // 엔티티를 dto로 변환
    Slice<MessageDto> dtoSlice = messageSlice.map(messageMapper::toDto);

    // 다음 커서 추출
    Instant nextCursor = null;
    if (!dtoSlice.getContent().isEmpty()) {
      nextCursor = dtoSlice.getContent().get(dtoSlice.getContent().size() - 1).createdAt();
    }

    // PageResponse dto로 변환
    PageResponse<MessageDto> response = pageResponseMapper.fromSlice(dtoSlice, nextCursor);

    return ResponseEntity.ok(response);
  }

  @Override
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    log.info("Received PATCH /api/messages/{} request", messageId); // 메시지 수정 요청 로그

    Message message = messageService.update(
        messageId,
        userDetails.getId(),
        request.newContent()
    );

    return ResponseEntity.ok(messageMapper.toDto(message));
  }

  @Override
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID messageId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    log.info("Received DELETE /api/messages/{} request", messageId); // 메시지 삭제 요청 로그

    messageService.deleteById(messageId, userDetails.getId());
    return ResponseEntity.noContent().build();
  }
}
