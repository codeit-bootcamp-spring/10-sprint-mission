package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestPart MessageCreateRequest messageCreateRequest,
                                              @RequestPart(required = false) List<MultipartFile> attachments) throws IOException {
        List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();

        if (attachments != null) {
            for (MultipartFile file : attachments) {
                BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                );
                binaryContentCreateRequests.add(request);
            }
        }
        messageCreateRequest = new MessageCreateRequest(
                messageCreateRequest.content(),
                messageCreateRequest.channelId(),
                messageCreateRequest.authorId(),
                binaryContentCreateRequests
        );

        Message message = messageService.create(messageCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages(@RequestParam UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);

    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(@PathVariable UUID messageId,
                                 @RequestBody MessageUpdateRequest messageUpdateRequest) {
        Message message = messageService.update(messageId, messageUpdateRequest);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();//204
    }



}
