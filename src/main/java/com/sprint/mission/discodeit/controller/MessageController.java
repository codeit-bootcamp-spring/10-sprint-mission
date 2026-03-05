package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.DeleteMessageResponseDTO;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageDto> createMessage(
            @RequestPart("messageCreateRequest") CreateMessageRequestDTO dto,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<CreateBinaryContentPayloadDTO> payloads = List.of();

        if (attachments != null && !attachments.isEmpty()) {
            payloads = attachments.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> {
                        try {
                            return new CreateBinaryContentPayloadDTO(
                                    file.getBytes(),
                                    file.getContentType(),
                                    file.getOriginalFilename(),
                                    file.getSize()
                            );
                        } catch (IOException e) {
                            throw new IllegalArgumentException("첨부 파일을 읽을 수 없습니다.", e);
                        }
                    })
                    .toList();
        }

        MessageDto created = messageService.createMessage(dto, payloads);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }


    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity updateMessage(
            @PathVariable UUID messageId,
            @RequestBody UpdateMessageRequestDTO dto
    ) {
        MessageDto updated = messageService.updateMessage(messageId, dto);

        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMessage(
            @PathVariable UUID messageId
            ) {
        messageService.deleteMessage(messageId);

        return ResponseEntity.ok(
                new DeleteMessageResponseDTO(
                        Instant.now(),
                        204,
                        "메시지가 성공적으로 삭제되었습니다."
                )
        );
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAllByChannelId(
            @RequestParam UUID channelId
    ) {
        List<MessageDto> messages = messageService.findAllByChannelId(channelId);

        return ResponseEntity.ok(messages);
    }

    @RequestMapping(value = "/by-user", method = RequestMethod.GET)
    public ResponseEntity findAllByUserId(
            @RequestParam UUID userId
    ) {
        List<MessageDto> messages = messageService.findAllByUserId(userId);

        return ResponseEntity.ok(messages);
    }
}
