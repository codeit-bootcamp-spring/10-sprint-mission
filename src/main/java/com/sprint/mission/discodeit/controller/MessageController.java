package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Message")
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    // 메시지 생성
    @Operation(summary = "Message 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "403", description = "Message를 생성할 권한이 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> createMessage(@RequestPart("messageCreateRequest") MessageDto.MessageCreateRequest messageReq,
                                                    @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.createMessage(messageReq, attachments));
    }

    // 메시지 채널 조회(UUID)
    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
    @RequestMapping(params = "channelId", method = RequestMethod.GET)
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(@RequestParam UUID channelId,
                                                                       @RequestParam(required = false) Instant cursor,
                                                                       @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(messageService.findAllByChannelId(channelId, cursor, pageable));
    }

    // 메시지 수정
    @Operation(summary = "Message 내용 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{message-id}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageDto> updateMessage(@PathVariable("message-id") UUID messageId,
                                                    @RequestBody MessageDto.MessageUpdateRequest messageReq) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(messageService.updateMessage(messageId, messageReq));
    }

    // 메시지 삭제
    @Operation(summary = "Message 내용 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{message-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable("message-id") UUID messageId) throws IOException {
        messageService.deleteMessage(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
