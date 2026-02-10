package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;


//    [ ] 메시지를 보낼 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Message> postMessage(@RequestBody MessageCreateRequest request) {
        Message created = messageService.create(request, List.of());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


//    [ ] 메시지를 수정할 수 있다.
    @RequestMapping(value = "/{message-id}", method = RequestMethod.PATCH)
    public ResponseEntity<Message> updateMessage(
            @PathVariable("message-id") UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        Message updated = messageService.update(messageId, request);
        return ResponseEntity.ok(updated);
    }


//    [ ] 메시지를 삭제할 수 있다.
    @RequestMapping(value = "/{message-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable("message-id") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }


//    [ ] 특정 채널의 메시지 목록을 조회할 수 있다.
    @RequestMapping(value = "/channels/{channel-id}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getMessagesByChannel(@PathVariable("channel-id") UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }
}
