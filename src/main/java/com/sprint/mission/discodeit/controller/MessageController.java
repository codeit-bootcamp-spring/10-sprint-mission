package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;
}
