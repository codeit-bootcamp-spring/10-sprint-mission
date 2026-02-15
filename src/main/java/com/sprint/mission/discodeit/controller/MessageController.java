package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    // 메시지 생성
    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public ResponseEntity<MessageResponseDTO> create(@RequestBody MessageCreateRequestDTO messageCreateRequestDTO) {
        MessageResponseDTO newMessage = messageService.create(messageCreateRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newMessage);
    }

    // 특정 채널에서 발행된 메시지 목록 조회
    @RequestMapping(value = "/channel/{channelId}/message", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponseDTO>> findAllByChannelId (@PathVariable UUID channelId) {
        List<MessageResponseDTO> messages = messageService.findAllByChannelId(channelId);

        return ResponseEntity.ok(messages);
    }

    // 메시지 수정
    @RequestMapping(value = "/message/{id}", method = RequestMethod.PUT)
    public ResponseEntity<MessageResponseDTO> update(@PathVariable UUID id,
                                                     @RequestBody MessageUpdateRequestDTO messageUpdateRequestDTO) {

        MessageResponseDTO updateMessage = messageService.update(id, messageUpdateRequestDTO);

        return ResponseEntity.ok(updateMessage);
    }

    // 메시지 삭제
    @RequestMapping(value = "/message/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResponseDTO> delete(@PathVariable UUID id) {
        messageService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
