package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    // 메시지 생성(보내기)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MessageResponseDTO> create(@RequestBody MessageCreateRequestDTO messageCreateRequestDTO) {
        MessageResponseDTO response = messageService.create(messageCreateRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 메시지 수정
    @RequestMapping(value = "/{message-id}", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity<MessageResponseDTO> update(@PathVariable("message-id") UUID messageId,
                                                     @RequestBody MessageUpdateRequestDTO messageUpdateRequestDTO) {
        MessageResponseDTO response = messageService.update(messageId, messageUpdateRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 메시지 삭제
    @RequestMapping(value = "/{message-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("message-id") UUID messageId) {
        messageService.delete(messageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 특정 채널 메시지 목록 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponseDTO>> findAllByChannelId(@RequestParam UUID channelId) {
        List<MessageResponseDTO> response = messageService.findAllByChannelId(channelId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
