package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public ResponseEntity<Message> postMessage(@RequestBody MessageCreateRequest request) {
        Message message = messageService.create(request, new ArrayList<>());
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/message/{messageId}", method = RequestMethod.PUT)
    public ResponseEntity<Message> putMessage(@PathVariable UUID messageId,
                                              @RequestBody MessageUpdateRequest request) {
        Message message = messageService.update(messageId, request);
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/channel/{channelId}/messages", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getMessageAllByChannelId(@PathVariable UUID channelId) {
        List<Message> messageList = messageService.findAllByChannelId(channelId);
        return new ResponseEntity<>(messageList, HttpStatus.OK);
    }

    @RequestMapping(value = "/message/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        System.out.println("메시지 삭제 시작");
        messageService.delete(messageId);
        System.out.println("메시지 삭제 완료");
        return ResponseEntity.ok().build();
    }
}
