package com.sprint.mission.discodeit.message.controller;

import com.sprint.mission.discodeit.message.dto.MessageCreateInfo;
import com.sprint.mission.discodeit.message.dto.MessageInfo;
import com.sprint.mission.discodeit.message.dto.MessageUpdateInfo;
import com.sprint.mission.discodeit.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<MessageInfo> sendMessage(@RequestBody MessageCreateInfo messageInfo) {
        return ResponseEntity.ok(messageService.createMessage(messageInfo));
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.GET)
    public ResponseEntity<MessageInfo> getMessage(@PathVariable UUID messageId) {
        return ResponseEntity.ok(messageService.findMessage(messageId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageInfo>> getAllMessages() {
        return ResponseEntity.ok(messageService.findAll());
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateInfo messageInfo
    ) {
        messageService.updateMessage(messageId, messageInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
