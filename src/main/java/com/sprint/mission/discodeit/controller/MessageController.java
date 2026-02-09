package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public MessageDto.Response create(@RequestBody MessageDto.CreateRequest request) {
        return messageService.create(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public MessageDto.Response update(@PathVariable UUID id,
                                      @RequestBody MessageDto.UpdateRequest request) {
        if (!id.equals(request.id())) throw new BusinessException(ErrorCode.PATH_ID_MISMATCH);

        return messageService.update(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID id) {
        messageService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<MessageDto.Response> findAllByChannelId(@RequestParam(name = "channelId") UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }
}
