package com.sprint.mission.discodeit.message.controller;

import com.sprint.mission.discodeit.common.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.common.dto.response.PageResponse;
import com.sprint.mission.discodeit.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.dto.response.MessageResponse;
import com.sprint.mission.discodeit.message.service.MessageService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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
@RequestMapping(value = "/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> create(
      @RequestPart @Valid MessageCreateRequest messageCreateRequest,
      @RequestPart(required = false) List<MultipartFile> attachments) {
    attachments = (attachments == null) ? List.of() : attachments;
    List<FileUploadRequest> safeAttachments = attachments.stream()
        .map(FileUploadRequest::from)
        .filter(Predicate.not(Objects::isNull))
        .toList();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(messageService.create(messageCreateRequest, safeAttachments));
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageResponse>> findInChannel(
      @RequestParam UUID channelId,
      @PageableDefault(size = 50, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(messageService.findSliceByChannelId(channelId, pageable));
  }

  @PatchMapping(value = "/{messageId}")
  public ResponseEntity<MessageResponse> update(
      @PathVariable UUID messageId,
      @RequestBody @Valid MessageUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(messageService.update(messageId, request));
  }

  @DeleteMapping(value = "/{messageId}")
  public ResponseEntity<?> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
