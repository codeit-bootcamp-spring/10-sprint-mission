package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageRequestCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestUpdateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 1. 메시지 생성
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponseDto> create(@RequestPart("request") MessageRequestCreateDto messageRequestDto,
                                                     @RequestPart(value = "attachments", required = false)
                                                     List<MultipartFile> attachments) {
        MessageResponseDto messageResponseDto = messageService.create(messageRequestDto, attachments);
        return ResponseEntity.ok(messageResponseDto);
    }

    // 2. 메시지 수정
    @RequestMapping(value = "/update", method = RequestMethod.PATCH)
    public ResponseEntity<MessageResponseDto> update(@RequestBody MessageRequestUpdateDto messageRequestDto) {
        MessageResponseDto messageResponseDto = messageService.update(messageRequestDto);
        return ResponseEntity.ok(messageResponseDto);
    }

    // 3. 메시지 삭제
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam UUID id) {
        messageService.delete(id);
        return ResponseEntity.ok().build();
    }

    // 4. 특정 채널의 메시지 목록 조회
    @RequestMapping(value = "/findByChannelId", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponseDto>> findByChannelId(@RequestParam UUID channelId) {
        List<MessageResponseDto> mrDto = messageService.findByChannelId(channelId);
        return ResponseEntity.ok(mrDto);
    }


}
