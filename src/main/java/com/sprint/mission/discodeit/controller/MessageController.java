package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public MessageResponseDto postMessage(
            @RequestPart("dto") MessageCreateRequestDto requestDto,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
            ){
        return messageService.create(requestDto, attachments);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public MessageResponseDto patchMessage(
            @RequestPart("dto") MessageUpdateRequestDto requestDto,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
            @PathVariable UUID id){
        return messageService.update(id, requestDto, attachments);
    }

}
