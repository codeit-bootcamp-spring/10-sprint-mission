package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 메시지 생성
    @RequestMapping(method = RequestMethod.POST)
    public MessageResponse postMessage(@RequestBody MessageCreateRequest dto) {
        return messageService.create(dto);
    }

    // 메시지 수정
    @RequestMapping(value = "/{messageId}",method = RequestMethod.PUT)
    public MessageResponse putMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest dto
    ){
        if(dto == null || dto.messageId() == null) {
            throw new IllegalArgumentException("messageId null이 될 수 없습니다.");
        }
        if(!messageId.equals(dto.messageId())) {
            throw new IllegalArgumentException("path messageId와 body messageId가 일치해야 합니다.");
        }
        return messageService.update(dto);

    }

    // 메시지 삭제
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public void deleteMessage(@PathVariable UUID messageId){
        messageService.delete(messageId);
    }

    // 특정 채널의 메시지 목록을 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<MessageResponse> getMessage(@RequestParam UUID channelId){
        return messageService.findAllByChannelId(channelId);
    }
}
