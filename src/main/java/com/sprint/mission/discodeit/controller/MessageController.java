package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    // TODO: @RequiredArgsConstructor로 리펙토링 고려

    // 메시지를 보낼 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageResponse> create(@RequestBody MessageCreateRequest request) {
        MessageResponse response = messageService.create(request);
        return ResponseEntity.ok(response); // ResponseEntity.status(HttpStatus.OK).body(responses); 축약형
    }

    //  메시지를 수정할 수 있다.
    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageResponse> update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        MessageResponse response = messageService.update(messageId, request);
        return ResponseEntity.ok(response);
    }

    // 메시지를 삭제할 수 있다.
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    // 특정 채널의 메시지 목록을 조회할 수 있다.
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> findAllByChannelId(@RequestParam UUID channelId) {
        List<MessageResponse> responses = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(responses);
    }
}
