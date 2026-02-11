package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.MessageWithBinaryRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    // 메시지 보내기
    @RequestMapping(value = "/api/messages", method = RequestMethod.POST)
    public ResponseEntity<Message> createMessage(@RequestBody MessageWithBinaryRequest request) {
        return ResponseEntity.ok(messageService.create(request.messageRequest(), request.binaryRequests()));
    }

    // 메시지 수정
    @RequestMapping(value = "/api/messages/{messageId}", method = RequestMethod.PATCH)
        public ResponseEntity<Message> updateMessage(@PathVariable UUID messageId,
                                                     @RequestBody MessageUpdateRequest messageUpdateRequest) {
            return ResponseEntity.ok(messageService.update(messageId, messageUpdateRequest));
        }

    // 메시지 삭제
    @RequestMapping(value = "/api/messages/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    // 특정 채널의 메시지 목록 조회
    @RequestMapping(value = "/api/messages", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getMessages(@RequestParam UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }
}
