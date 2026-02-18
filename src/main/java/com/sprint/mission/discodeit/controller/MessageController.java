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
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    //메시지를 보낼 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(@ModelAttribute MessageDto.Create request) {
        MessageDto.Response response = messageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //메시지를 수정할 수 있다.
    @RequestMapping(value = "/{authorId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> update(
            @PathVariable UUID authorId,
            @ModelAttribute MessageDto.Update request
    ) {
        MessageDto.Response response = messageService.update(authorId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //메시지를 삭제할 수 있다.
    @RequestMapping(value = "/{authorId}/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(
            @PathVariable UUID authorId,
            @PathVariable UUID messageId
    ) {
        messageService.delete(authorId, messageId);
        return ResponseEntity.noContent().build();
    }

    //특정 채널의 메시지 목록을 조회할 수 있다.
    @RequestMapping(value = "/channel/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<?> findAllByChannelId(
            @PathVariable UUID channelId
    ) {
        List<MessageDto.Response> responses = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    //특정 메시지 조회
    @RequestMapping(value = "/{messageId}", method = RequestMethod.GET)
    public ResponseEntity<?> findById(
            @PathVariable UUID messageId
    ) {
        MessageDto.Response response = messageService.findById(messageId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
