package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    /**
     * @param request      메시지 본문 및 관련 ID (JSON)
     * @param attachments  첨부파일 리스트 (선택 사항)
     */

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto.Response> createMessage(
            @RequestPart("request") @Valid MessageDto.CreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        MessageDto.Response response = messageService.create(request, attachments);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{message-id}")
    public ResponseEntity<MessageDto.Response> updateMessage(
            @PathVariable("message-id") UUID messageId,
            @RequestBody @Valid MessageDto.UpdateRequest request) {
        MessageDto.Response response = messageService.update(messageId, request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{message-id}")
    public ResponseEntity<MessageDto.Response> findMessage(@PathVariable("message-id") UUID messageId) {
        MessageDto.Response response = messageService.find(messageId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{message-id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("message-id") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto.Response>> findAllByChannel(@RequestParam("channel-id") UUID channelId) {
        List<MessageDto.Response> response = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(response);
    }

}
