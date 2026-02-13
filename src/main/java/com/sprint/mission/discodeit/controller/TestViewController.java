package com.sprint.mission.discodeit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestViewController {

    @GetMapping("/users")
    public String userManagement() {
        return "test/users"; // templates/test/users.html
    }

    @GetMapping("/channels")
    public String channelManagement() {
        return "test/channels"; // templates/test/channels.html
    }

    @GetMapping("/messages")
    public String messageManagement() {
        return "test/messages"; // templates/test/messages.html
    }
}