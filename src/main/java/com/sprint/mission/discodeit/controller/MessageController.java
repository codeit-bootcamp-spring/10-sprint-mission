package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> postMessage(@RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
                                               @RequestPart(value = "attachments", required = false)List<MultipartFile> attachments) {
        System.out.println(messageCreateRequest);
        List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();

        if (attachments != null && !attachments.isEmpty()) {
            try {
                for (MultipartFile file : attachments) {
                    BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getBytes()
                    );
                    binaryContentCreateRequests.add(request);
                }
            } catch (IOException e) {
                throw new RuntimeException("파일 변환 실패", e);
            }
        }

        Message message = messageService.create(messageCreateRequest, binaryContentCreateRequests);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity<Message> putMessage(@PathVariable UUID messageId,
                                              @RequestBody MessageUpdateRequest request) {
        Message message = messageService.update(messageId, request);
        return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getMessageAllByChannelId(@RequestParam UUID channelId) {
        List<Message> messageList = messageService.findAllByChannelId(channelId);
        return new ResponseEntity<>(messageList, HttpStatus.OK);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        System.out.println("메시지 삭제 시작");
        messageService.delete(messageId);
        System.out.println("메시지 삭제 완료");
        return ResponseEntity.ok().build();
    }
}
