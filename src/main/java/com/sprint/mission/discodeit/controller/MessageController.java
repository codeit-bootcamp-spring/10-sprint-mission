package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateDocRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/messages")
public class MessageController {

  private final MessageService messageService;
  private final BinaryContentService binaryContentService;

  public MessageController(MessageService messageService,
      BinaryContentService binaryContentService) {
    this.messageService = messageService;
    this.binaryContentService = binaryContentService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<MessageResponse>> findAllByChannelId(@RequestParam UUID channelId) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
  }

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> create(
      @RequestPart("messageCreateRequest") MessageCreateDocRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) throws IOException {

    List<UUID> attachmentIds = uploadAttachments(attachments);

    MessageCreateRequest req = new MessageCreateRequest(
        messageCreateRequest.channelId(),
        messageCreateRequest.authorId(),   // 문서 authorId -> 내부 userId 자리로 매핑
        messageCreateRequest.content(),
        attachmentIds
    );

    MessageResponse created = messageService.create(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
  public ResponseEntity<MessageResponse> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest body
  ) {
    MessageUpdateRequest req = new MessageUpdateRequest(messageId, body.newContent());
    return ResponseEntity.ok(messageService.update(req));
  }

  @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  private List<UUID> uploadAttachments(List<MultipartFile> attachments) throws IOException {
    if (attachments == null || attachments.isEmpty()) {
      return List.of();
    }

    List<UUID> ids = new ArrayList<>();
    for (MultipartFile file : attachments) {
      if (file == null || file.isEmpty()) {
        continue;
      }

      UUID id = binaryContentService.create(new BinaryContentCreateRequest(
          file.getOriginalFilename(),
          file.getContentType(),
          file.getBytes(),
          null,
          null
      ));
      ids.add(id);
    }
    return List.copyOf(ids);
  }
}