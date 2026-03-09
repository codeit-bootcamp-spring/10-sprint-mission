package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;
  private final ObjectMapper objectMapper;

  // mapper 추가'
  private final MessageMapper messageMapper;

  // POST /api/messages -> 201
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
          @RequestPart("messageCreateRequest") String messageCreateRequestJson,
          @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) throws Exception {
    MessageCreateRequest messageCreateRequest =
            objectMapper.readValue(messageCreateRequestJson, MessageCreateRequest.class);

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

    Message createdMessage = messageService.create(messageCreateRequest, attachmentRequests);

    // Entity -> Dto로
    MessageDto dto = messageMapper.toDto(createdMessage);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // PATCH /api/messages/{messageId}
  @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
  public ResponseEntity<MessageDto> update(
          @PathVariable UUID messageId,
          @RequestBody MessageUpdateRequest request
  ) {
    Message updatedMessage = messageService.update(messageId, request);
    MessageDto dto = messageMapper.toDto(updatedMessage);
    return ResponseEntity.ok(dto);
  }

  // DELETE /api/messages/{messageId} -> 204
  @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  // GET /api/messages?channelId
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<MessageDto>> findAllByChannelId(@RequestParam("channelId") UUID channelId) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    List<MessageDto> dtos = messages.stream()
            .map(messageMapper::toDto)
            .toList();

    return ResponseEntity.ok(dtos);
  }
}
