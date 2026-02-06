package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 메시지 생성
    @RequestMapping(method= RequestMethod.POST)
    public MessageResponse postMessage(@RequestBody MessageCreateRequest request){
        return messageService.create(request);
    }

    // 메시지 수정
    @RequestMapping(method=RequestMethod.PATCH)
    public MessageResponse updateMessage(@RequestBody MessageUpdateRequest request){
        return messageService.update(request);
    }

    // 메시지 삭제
    @RequestMapping(value="/{message-id}", method=RequestMethod.DELETE)
    public void deleteMessage(@PathVariable("message-id") UUID messageID){
        messageService.deleteMessage(messageID);
    }

    // 특정 Channel 메시지 목록 조회
    @RequestMapping(method=RequestMethod.GET)
    public List<MessageResponse> getAllMessages(@RequestParam("channel-id") UUID channelID){
        return messageService.findMessagesByChannel(channelID);
    }
}
