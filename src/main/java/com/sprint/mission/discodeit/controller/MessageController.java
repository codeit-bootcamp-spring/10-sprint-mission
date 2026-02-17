package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels/{channelId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageDto> create(@PathVariable UUID channelId,
                                             @RequestHeader UUID authorId,
                                             @Valid @RequestBody MessageCreateRequest messageCreateRequest) {
        MessageDto response = messageService.create(channelId, authorId, messageCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto>> findAllByChannelId(@PathVariable UUID channelId,
                                                               @RequestHeader UUID userId) {
        List<MessageDto> responses = messageService.findAllByChannelId(channelId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageDto> update(@PathVariable UUID channelId,
                                             @RequestHeader UUID authorId,
                                             @PathVariable UUID messageId,
                                             @Valid @RequestBody MessageUpdateRequest messageUpdateRequest) {
        MessageDto response = messageService.update(channelId, authorId, messageId, messageUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID channelId,
                                       @RequestHeader UUID userId,
                                       @PathVariable UUID messageId) {
        messageService.delete(channelId, userId, messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
