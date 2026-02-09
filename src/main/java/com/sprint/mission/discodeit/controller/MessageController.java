package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;


    // 메세지 생성
    // senderid, channelId, Content, attachment
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageResponse> create(@RequestBody MessageCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.createMessage(request));
    }

    //메세지 수정 (내용)
    @RequestMapping(value="/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageResponse> update(@PathVariable UUID messageId,
                                                  @RequestBody MessageUpdateRequest body){
        MessageUpdateRequest request = new MessageUpdateRequest(messageId, body.newContent());
        return ResponseEntity.ok(messageService.updateMessage(request));
    }

    //메세지 삭제
    @RequestMapping(value="/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResponse> delete (@PathVariable UUID messageId){
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    // 특정 채널의 메세지 리스트 조회
    @RequestMapping(value = "{channelId}/messages", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> findAllByChannelId(@PathVariable UUID channelId){
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

}
