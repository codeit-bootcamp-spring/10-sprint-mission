package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 메세지 보내기
    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<MessageDto.MessageResponse> createMessage(
            @RequestBody MessageDto.MessageCreateRequest request
    ) {
        MessageDto.MessageResponse created = messageService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 메시지 수정
    @RequestMapping(method = RequestMethod.PUT, path = "/{messageId}")
    public ResponseEntity<MessageDto.MessageResponse> updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageDto.MessageUpdateRequest request
    ) {
        MessageDto.MessageResponse updated = messageService.update(messageId,request);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // 메시지 삭제
    @RequestMapping(method = RequestMethod.DELETE, path = "/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    // 채널의 메세지 목록 조회
    @RequestMapping(method = RequestMethod.GET, path = "")
    public ResponseEntity<List<MessageDto.MessageResponse>> findAllByChannelId(
            @RequestParam UUID channelId
    ) {
        List<MessageDto.MessageResponse> messages = messageService.findAllByChannelId(channelId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    // 메세지 조회
    @RequestMapping(method = RequestMethod.GET, path = "/{messageId}")
    public ResponseEntity<MessageDto.MessageResponse> findById(
            @PathVariable UUID messageId
    ) {
        MessageDto.MessageResponse message = messageService.findById(messageId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
