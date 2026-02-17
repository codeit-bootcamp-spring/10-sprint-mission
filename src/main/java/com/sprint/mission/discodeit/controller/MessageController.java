package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    // 메시지 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageDto.response> createMessage(@RequestPart("message") MessageDto.createRequest messageReq,
                                                             @RequestPart(value = "attachment", required = false) List<MultipartFile> attachments) throws IOException {
        List<BinaryContentDto.createRequest> contentReqs = toServiceDto(attachments);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.createMessage(messageReq, contentReqs));
    }

    // 메시지 단일 조회(UUID)
    @RequestMapping(value = "/{message-id}", method = RequestMethod.GET)
    public ResponseEntity<MessageDto.response> findMessage(@PathVariable("message-id") UUID messageId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(messageService.findMessage(messageId));
    }

    // 메시지 채널 조회(UUID)
    @RequestMapping(value = "/findAll/{channel-id}", method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto.response>> findAllByChannelId(@PathVariable("channel-id") UUID channelId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(messageService.findAllByChannelId(channelId));
    }

    // 메시지 수정
    @RequestMapping(value = "/{message-id}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageDto.response> updateMessage(@PathVariable("message-id") UUID messageId,
                                                             @RequestBody MessageDto.updateRequest messageReq) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(messageService.updateMessage(messageId, messageReq));
    }

    // 메시지 삭제
    @RequestMapping(value = "/{message-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> updateMessage(@PathVariable("message-id") UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private List<BinaryContentDto.createRequest> toServiceDto(List<MultipartFile> attachments) throws IOException {
        if (attachments == null) return null;

        List<BinaryContentDto.createRequest> results = new ArrayList<>();
        for (MultipartFile attachment : attachments) {
            results.add(new BinaryContentDto.createRequest(BinaryContentType.fromMimeType(attachment.getContentType()),
                    attachment.getOriginalFilename(), attachment.getBytes()));
        }

        return results;
    }
}
