package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.input.MessageCreateInput;
import com.sprint.mission.discodeit.dto.message.input.MessageUpdateInput;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 메시지 관리 Controller
 */
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MessageController {
    private final MessageService messageService;

    /**
     * 메시지 전송(생성)
     */
    @RequestMapping(value = "/channels/{channelId}/message", method = RequestMethod.POST)
    public ResponseEntity createMessage(@PathVariable UUID channelId,
                                        @RequestBody @Valid MessageCreateRequest request) {
        MessageCreateInput input = new MessageCreateInput(channelId, request.authorId(),
                request.content(), request.attachments());
        Message message = messageService.createMessage(input);
        MessageResponse result = createMessageResponse(message);

        return ResponseEntity.status(201).body(result);
    }

    /**
     * 특정 채널 메시지 목록 조회
     */
    @RequestMapping(value = "/channels/{channelId}/messages", method = RequestMethod.GET)
    public ResponseEntity findAllMessagesByChannelId(@PathVariable UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        List<MessageResponse> result = messages.stream().map(m -> createMessageResponse(m)).toList();

        return ResponseEntity.status(200).body(result);
    }

    /**
     * 메시지 수정
     */
    @RequestMapping(value = "/messages/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity updateMessage(@PathVariable UUID messageId,
                                        @RequestBody @Valid MessageUpdateRequest request) {
        MessageUpdateInput input = new MessageUpdateInput(messageId, request.requestUserId(), request.content());
        Message message = messageService.updateMessageContent(input);
        MessageResponse result = createMessageResponse(message);

        return ResponseEntity.status(200).body(result);
    }

    /**
     * 메시지 삭제
     */
    @RequestMapping(value = "/messages/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMessage(@PathVariable UUID messageId,
                                        @RequestBody UUID userId) {
        messageService.deleteMessage(userId, messageId);
        return ResponseEntity.status(204).build();
    }

    private MessageResponse createMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(), message.getChannel().getId(),
                message.getAuthor().getId(), message.getCreatedAt(),
                message.getUpdatedAt(), message.getContent(), message.getAttachmentIds());
    }
}
