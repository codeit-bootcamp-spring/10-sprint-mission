package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentProcessingException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;
  private final ObjectMapper objectMapper;

  // mapper 추가
  private final MessageMapper messageMapper;

  // POST /api/messages -> 201
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
          @RequestPart("messageCreateRequest") String messageCreateRequestJson,
          @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) throws Exception {
    log.info("HTTP 요청 - 메세지 생성");

    MessageCreateRequest messageCreateRequest =
            objectMapper.readValue(messageCreateRequestJson, MessageCreateRequest.class);

    List<BinaryContentCreateRequest> attachmentRequests =
        Optional.ofNullable(attachments)
            .map(
                files ->
                    files.stream()
                        .map(
                            file -> {
                              try {
                                return new BinaryContentCreateRequest(
                                    file.getOriginalFilename(),
                                    file.getContentType(),
                                    file.getBytes());
                              } catch (IOException e) {
                                throw new BinaryContentProcessingException("JSON parsing failed");
                              }
                            })
                        .toList())
            .orElse(new ArrayList<>());

    Message createdMessage = messageService.create(messageCreateRequest, attachmentRequests);

    log.info("HTTP 응답 - 메세지 생성 완료 messeageId={}", createdMessage.getId());
    // Entity -> Dto로
    MessageDto dto = messageMapper.toDto(createdMessage);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // PATCH /api/messages/{messageId}
  @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
  public ResponseEntity<MessageDto> update(
          @PathVariable UUID messageId,
          @Valid @RequestBody MessageUpdateRequest request
  ) {
    log.info("HTTP 요청 - 메시지 수정 messageId={}", messageId);

    Message updatedMessage = messageService.update(messageId, request);
    MessageDto dto = messageMapper.toDto(updatedMessage);
    log.info("HTTP 응답 - 메시지 수정 완료 messageId={}", messageId);
    return ResponseEntity.ok(dto);
  }

  // DELETE /api/messages/{messageId} -> 204
  @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    log.info("HTTP 요청 - 메시지 삭제 messageId={}", messageId);
    messageService.delete(messageId);

    log.info("HTTP 응답 - 메시지 삭제 완료 messageId={}", messageId);
    return ResponseEntity.noContent().build();
  }

  // GET /api/messages?channelId
  // v 커서 페이징 적용 부분.
  @RequestMapping(method = RequestMethod.GET)
  public PageResponse<MessageDto> findAllByChannel_Id(
          @RequestParam UUID channelId,
          @RequestParam(required = false) Instant cursor,
          @RequestParam(defaultValue = "50") int size
  ) {

    log.debug("HTTP 요청 - 메시지 조회 channelId={}, cursor={}, size={}", channelId, cursor, size);

    List<Message> messages = messageService.findAllByChannel_Id(channelId, cursor, size);

    boolean hasNext = messages.size() > size;
    List<Message> pageContent = hasNext ? messages.subList(0, size) : messages;

    List<MessageDto> content = pageContent.stream()
            .map(messageMapper::toDto)
            .toList();

    Instant nextCursor = null;
    if (!pageContent.isEmpty() && hasNext) {
      nextCursor = pageContent.get(pageContent.size() - 1).getCreatedAt();
    }

    return new PageResponse<>(
            content,
            nextCursor,
            size,
            hasNext,
            null
    );
  }
}
