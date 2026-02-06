package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    /*
     **메시지 관리**
    - [ ]  메시지를 보낼 수 있다.
    - [ ]  메시지를 수정할 수 있다.
    - [ ]  메시지를 삭제할 수 있다.
    - [ ]  특정 채널의 메시지 목록을 조회할 수 있다.
     */
    private final MessageService messageService;

    @RequestMapping(path = "/{channelid}", method = RequestMethod.POST)
    public ResponseEntity<MessageResponseDto> sendMessage(
            @RequestHeader UUID userId,
            @PathVariable UUID channelid,
            @RequestBody MessageCreateDto dto)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(channelid,userId,dto));
    }

    @RequestMapping(path = "/{messageid}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageResponseDto> updateMessage(
            @RequestHeader UUID userId,
            @PathVariable UUID messageid,
            @RequestBody MessageUpdateDto dto)
    {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(messageService.update(messageid,userId,dto));
    }

    @RequestMapping(path = "/{messageid}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMessage(
            @RequestHeader UUID userId,
            @PathVariable UUID messageid
            )
    {
        messageService.delete(messageid,userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/{channelid}", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponseDto>> updateMessage(
            @RequestHeader UUID userId,
            @PathVariable UUID channelid)
    {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.findAllByChannelId(userId,channelid));
    }

}
