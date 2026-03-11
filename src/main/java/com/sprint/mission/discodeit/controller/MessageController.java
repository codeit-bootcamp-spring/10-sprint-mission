package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Operation(summary = "메시지 전송(생성)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "message created",
                    content = @Content(
                            schema = @Schema(implementation = MessageDto.class)
                    )
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> sendMessage(@RequestPart(value = "messageCreateRequest") MessageCreateRequest messageCreateRequest,
                                                  @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) throws IOException {
        List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();

        if (attachments != null) {
            for (MultipartFile file : attachments) {
                BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                );
                binaryContentCreateRequests.add(request);
            }
        }
        messageCreateRequest = new MessageCreateRequest(
                messageCreateRequest.content(),
                messageCreateRequest.channelId(),
                messageCreateRequest.authorId(),
                binaryContentCreateRequests
        );

        Message message = messageService.create(messageCreateRequest);
        MessageDto messageDto = messageMapper.toDto(message);

        return ResponseEntity.status(HttpStatus.CREATED).body(messageDto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> getAllMessages(@RequestParam UUID channelId,
                                                                   @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC)
                                                                    Pageable pageable) { //Postman에서 page,size,sort를 보내면 자동으로 pageable로 들어옴.

        Page<Message> messages = messageService.findAllByChannelId(channelId,pageable);
        Page<MessageDto> messageDtos = messages.map(messageMapper::toDto);
        PageResponse<MessageDto> response = pageResponseMapper.fromPage(messageDtos);

        return ResponseEntity.ok(response);

    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageDto> updateMessage(@PathVariable UUID messageId,
                                 @RequestBody MessageUpdateRequest messageUpdateRequest) {
        Message message = messageService.update(messageId, messageUpdateRequest);
        MessageDto messageDto = messageMapper.toDto(message);
        return ResponseEntity.ok(messageDto);
    }

    @Operation(summary = "메시지 삭제")
    @ApiResponse(
            responseCode = "204",
            description = "message delete"
    )
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();//204
    }



}
