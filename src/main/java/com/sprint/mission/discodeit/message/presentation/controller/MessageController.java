package com.sprint.mission.discodeit.message.presentation.controller;

import com.sprint.mission.discodeit.common.api.response.PageResponse;
import com.sprint.mission.discodeit.message.application.service.MessageService;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.response.MessageResponse;
import com.sprint.mission.discodeit.message.presentation.mapper.MessageApiMapper;
import jakarta.validation.Valid;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;
  private final MessageApiMapper mapper;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MessageResponse> create(
      @RequestBody @Valid MessageCreateRequest messageCreateRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(messageService.create(messageCreateRequest)));
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageResponse>> findInChannel(
      @RequestParam UUID channelId,
      @PageableDefault(size = 50, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(mapper.fromSlice(messageService.findSliceByChannelId(channelId, pageable)));
  }

  @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MessageResponse> update(
      @PathVariable UUID id,
      @RequestBody @Valid MessageUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(mapper.toDto(messageService.update(id, request)));
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> delete(@PathVariable UUID id) {
    messageService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
