package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.AllArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;
/*
    @RequestMapping(method = RequestMethod.POST)
    public void postMessage(
            @RequestPart("dto") MessageCreateRequestDto
            ){

    }
    */
}
