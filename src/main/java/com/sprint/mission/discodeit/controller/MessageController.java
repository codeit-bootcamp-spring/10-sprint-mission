package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public MessageResponseDTO sendMessage(@RequestBody MessageCreateRequestDTO req){
        return messageService.create(req);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    @ResponseBody
    public MessageResponseDTO editMessage(@RequestBody MessageUpdateRequestDto req){
        return messageService.update(req);
    }

    @RequestMapping(value = "/{messageId}",method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteMessage(@PathVariable UUID messageId){
        messageService.delete(messageId);
    }


    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    @ResponseBody
    public List<MessageResponseDTO> viewChannelMessage(@PathVariable UUID channelId){
        return messageService.findallByChannelId(channelId);
    }





}
