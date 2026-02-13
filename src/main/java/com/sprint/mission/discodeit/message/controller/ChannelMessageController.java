package com.sprint.mission.discodeit.message.controller;

import com.sprint.mission.discodeit.message.dto.MessageInfo;
import com.sprint.mission.discodeit.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/channels/{channelId}/messages")
public class ChannelMessageController {
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageInfo>> getMessages(@PathVariable UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }
}
