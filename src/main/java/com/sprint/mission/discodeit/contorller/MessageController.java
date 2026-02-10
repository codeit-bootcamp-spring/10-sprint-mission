package com.sprint.mission.discodeit.contorller;


import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UUID> createMessage(
            @RequestParam UUID userId,
            @RequestBody CreateMessageRequest request
    ) {
        UUID messageId = messageService.createMessage(userId, request);

        return ResponseEntity.ok(messageId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> findAllMessagesByChannelId(
            @RequestParam UUID channelId
    ) {
        List<MessageResponse> responses =
                messageService.findAllMessagesByChannelId(channelId);

        return ResponseEntity.ok(responses);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<MessageResponse> updateMessage(
            @RequestParam UUID userId,
            @RequestBody UpdateMessageRequest request
    ) {
        MessageResponse response =
                messageService.updateMessage(userId, request);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(
            @RequestParam UUID userId,
            @PathVariable UUID messageId
    ) {
        messageService.deleteMessage(userId, messageId);

        return ResponseEntity.noContent().build();
    }
}
