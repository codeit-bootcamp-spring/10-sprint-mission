package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Message sendMessage(@RequestBody MessageCreateRequest messageCreateRequest) {
        return messageService.create(messageCreateRequest);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public Message updateMessage(@PathVariable UUID id,
                                 @RequestBody MessageUpdateRequest messageUpdateRequest) {
        return messageService.update(id,messageUpdateRequest);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteMessage(@PathVariable UUID id) {
        messageService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Message> getAllMessages(@RequestParam UUID channelId) {
        return messageService.findAllByChannelId(channelId);

    }

}
