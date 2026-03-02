package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message API")
public class MessageController {

    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Message 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = {
                            @Encoding(name = "messageCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE),
                            @Encoding(name = "attachments", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    }
            )
    )
    public ResponseEntity<Message> createMessage(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<BinaryContentRequest> attachmentRequests = new ArrayList<>();

        if (attachments != null) {
            for (MultipartFile file : attachments) {
                attachmentRequests.add(
                        BinaryContentRequest.of(BinaryContentOwnerType.MESSAGE, file)
                );
            }
        }

        Message response = messageService.createMessage(
                messageCreateRequest.authorId(),
                messageCreateRequest,
                attachmentRequests
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
    public ResponseEntity<List<Message>> findAllMessagesByChannelId(
            @Parameter(description = "조회할 Channel ID", example = "6e4be7c3-e196-447e-8b95-558e365fc01d")
            @RequestParam UUID channelId
    ) {
        List<Message> responses = messageService.findAllMessagesByChannelId(channelId);

        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{messageId}")
    @Operation(summary = "Message 내용 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
    })
    public ResponseEntity<Message> updateMessage(
            @Parameter(description = "수정할 Message ID", example = "9b0712f3-7dbf-4e9f-9f33-63cdb028b07a")
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        Message response = messageService.updateMessage(messageId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "Message 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "삭제할 Message ID", example = "9b0712f3-7dbf-4e9f-9f33-63cdb028b07a")
            @PathVariable UUID messageId
    ) {
        messageService.deleteMessage(messageId);

        return ResponseEntity.noContent().build();
    }
}
