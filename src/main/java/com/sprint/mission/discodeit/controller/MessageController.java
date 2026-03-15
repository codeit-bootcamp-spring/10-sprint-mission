package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.controller.support.BinaryContentRequestResolver;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;
  private final BinaryContentRequestResolver binaryContentRequestResolver;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<MessageDto> create(
          @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
          @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(messageService.create(
                    messageCreateRequest,
                    binaryContentRequestResolver.resolveList(attachments)
            ));
  }

  @PatchMapping("{messageId}")
  @Override
  public ResponseEntity<MessageDto> update(
          @PathVariable("messageId") UUID messageId,
          @RequestBody MessageUpdateRequest request
  ) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(messageService.update(messageId, request));
  }

  @DeleteMapping("{messageId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
  }

  @GetMapping
  @Override
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
          @RequestParam("channelId") UUID channelId,
          @RequestParam(value = "cursorCreatedAt", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant cursorCreatedAt,
          @RequestParam(value = "cursorId", required = false) UUID cursorId,
          @RequestParam(value = "size", defaultValue = "50") Integer size
  ) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(messageService.findAllByChannelId(channelId, cursorCreatedAt, cursorId, size));
  }
}