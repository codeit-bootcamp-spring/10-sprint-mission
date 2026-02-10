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
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    // 메시지 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageResponseDto> postMessage(@RequestBody MessageCreateDto dto){
        MessageResponseDto response = messageService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 메시지 조회
    @RequestMapping(value = "/message/{id}", method = RequestMethod.GET)
    public ResponseEntity<MessageResponseDto> getMessage(@PathVariable UUID id){
        MessageResponseDto response = messageService.findMessage(id);
        return ResponseEntity.ok(response);
    }

    // 채널 내 메시지 조회(키워드 따라 조회 가능)
    @RequestMapping(value= "/{channel-id}", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponseDto>> getAllMessage(@PathVariable("channel-id") UUID id,
                                                                  @RequestParam(required = false) String keyword){
        if(keyword == null){
            return ResponseEntity.ok(messageService.findAllMessagesByChannelId(id));
        } else{
            return ResponseEntity.ok(messageService.findMessageByKeyword(id,keyword));
        }

    }

    // 메시지 업데이트
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageResponseDto> updateMessage(@PathVariable UUID id,
                                                      @RequestBody MessageUpdateDto dto){
        MessageResponseDto response = messageService.update(id,dto);
        return ResponseEntity.ok(response);
    }

    // 메시지 삭제
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id){
        messageService.delete(id);
        return ResponseEntity.noContent().build(); // noContent는 객체를 만든다는 뜻, build를 붙혀서 객체 만듬

    }
}
