package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(@RequestBody MessageCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateMessage(
            @PathVariable UUID id,
            @RequestBody MessageUpdateRequest request){
        return ResponseEntity.ok(messageService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id){
        messageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<MessageResponse>> findAllByChannel(
            @PathVariable UUID channelId,
            @RequestParam UUID userId){
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId, userId));
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<MessageResponse> togglePin(@PathVariable UUID id){
        return ResponseEntity.ok(messageService.togglePin(id));
    }
}
