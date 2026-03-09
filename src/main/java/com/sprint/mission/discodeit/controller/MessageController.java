package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.DeleteMessageResponseDTO;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.response.PageResponse;
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
import java.util.Arrays;
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
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    ) {
        List<CreateBinaryContentPayloadDTO> payloads = List.of();

        if (attachments != null && attachments.length > 0) {
            payloads = Arrays.stream(attachments)
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

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAllByChannelId(
            @RequestParam UUID channelId,
            @RequestParam(defaultValue = "1") int page
    ) {
        PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, page - 1);

        return ResponseEntity.ok(messages);
    }

    @RequestMapping(value = "/by-user", method = RequestMethod.GET)
    public ResponseEntity findAllByUserId(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "1") int page
    ) {
        PageResponse<MessageDto> messages = messageService.findAllByUserId(userId, page - 1);

        return ResponseEntity.ok(messages);
    }
}
