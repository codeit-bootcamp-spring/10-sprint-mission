package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageServiceDTO.*;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> create(@RequestPart @Valid MessageCreateRequest messageCreateRequest,
                                                  @RequestPart(required = false) @Nullable List<MultipartFile> attachments) {
        if (attachments == null) {
            attachments = List.of();
        }
        MessageCreateCommand command = new MessageCreateCommand(messageCreateRequest, attachments);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(command));
    }

    @GetMapping
    public ResponseEntity<List<MessageResponse>> findInChannel(@RequestParam UUID channelId) {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.findAllByChannelId(channelId));
    }

    @PatchMapping(value = "/{messageId}")
    public ResponseEntity<MessageResponse> update(@PathVariable UUID messageId,
                                                  @RequestBody MessageUpdateRequest request) {
        MessageUpdateCommand command = new MessageUpdateCommand(messageId, request);
        return ResponseEntity.status(HttpStatus.OK).body(messageService.update(command));
    }

    @DeleteMapping(value = "/{messageId}")
    public ResponseEntity<?> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
