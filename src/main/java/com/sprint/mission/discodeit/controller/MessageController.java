package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createMessage(@RequestBody MessageCreateRequest request) {
        return messageService.createMessage(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<MessageResponse> getMessages(@RequestParam UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<MessageResponse> getAllMessages() {
        return messageService.getAllMessages();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse getMessage(@PathVariable UUID id) {
        return messageService.getMessage(id);
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<MessageResponse> getMessagesByUser(@PathVariable UUID userId) {
        return messageService.getMessagesByUserId(userId);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse updateMessage(@RequestBody MessageUpdateRequest request) {
        return messageService.updateMessage(request);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteMessage(@RequestParam UUID id) {
        messageService.deleteMessage(id);
    }
}