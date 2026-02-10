package com.sprint.mission.discodeit.message.controller;


import com.sprint.mission.discodeit.message.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.MessageResponse;
import com.sprint.mission.discodeit.message.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public MessageResponse createMessage (@RequestBody MessageCreateRequest request){
        return messageService.create(request);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public MessageResponse updateMessage (@RequestBody MessageUpdateRequest request){
        return messageService.update(request);
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public void deleteMessage(@PathVariable UUID id){
        messageService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET, params = "channelId")
    public List<MessageResponse> findAllByChannelMessage(@RequestParam UUID channelId){
        return messageService.findAllByChannelId(channelId);
    }
}
