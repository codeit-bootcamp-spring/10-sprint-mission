package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.DeleteMessageResponseDTO;
import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createMessage(
            @RequestBody CreateMessageRequestDTO dto
    ) {
        MessageResponseDTO created = messageService.createMessage(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.messageId())
                .toUri();

        return ResponseEntity.created(location)
                .body(created);
    }

    @RequestMapping(value = "/update/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity updateMessage(
            @PathVariable UUID messageId,
            @RequestBody UpdateMessageRequestDTO dto
    ) {
        MessageResponseDTO updated = messageService.updateMessage(messageId, dto);

        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMessage(
            @PathVariable UUID messageId
            ) {
        messageService.deleteMessage(messageId);

        return ResponseEntity.ok(
                new DeleteMessageResponseDTO(
                        Instant.now(),
                        200,
                        "메시지가 성공적으로 삭제되었습니다."
                )
        );
    }

    @RequestMapping(value = "/by-channel", method = RequestMethod.GET)
    public ResponseEntity findAllByChannelId(
            @RequestParam UUID channelId
    ) {
        List<MessageResponseDTO> messages = messageService.findAllByChannelId(channelId);

        return ResponseEntity.ok(messages);
    }

    @RequestMapping(value = "/by-user", method = RequestMethod.GET)
    public ResponseEntity findAllByUserId(
            @RequestParam UUID userId
    ) {
        List<MessageResponseDTO> messages = messageService.findAllByUserId(userId);

        return ResponseEntity.ok(messages);
    }
}
