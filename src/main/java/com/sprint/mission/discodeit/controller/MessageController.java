package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 메시지 관리 Controller
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Message", description = "Message API")
public class MessageController {
    private final MessageService messageService;

    /**
     * 메시지 전송(생성)
     */
    @RequestMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method = RequestMethod.POST)
    @Operation(summary = "Message 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(examples = @ExampleObject("Channel | Author with id {channelId | authorId} not found")))
    })
    public ResponseEntity<MessageDto> create(
            @RequestPart("messageCreateRequest") @Valid MessageCreateRequest request,
            @RequestPart(required = false) @Schema(description = "Message 첨부 파일들") List<MultipartFile> attachments
    ) {
        MessageDto message = messageService.create(request, attachments);

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    /**
     * 특정 채널 메시지 목록 조회
     */
    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
            @Parameter(description = "조회할 Channel ID") @RequestParam UUID channelId,
            @Parameter(description = "페이징 커서 정보") @RequestParam(required = false) Instant cursor,
            @Parameter(description = "페이징 정보", example = "{\"size\": 50, \"sort\": \"createdAt,desc\"}")
            @PageableDefault(size = 50, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    /**
     * 메시지 수정
     */
    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    @Operation(summary = "Message 내용 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(examples = @ExampleObject("Message with id {messageId} not found")))
    })
    public ResponseEntity<MessageDto> update(
            @Parameter(description = "수정할 Message ID") @PathVariable UUID messageId,
            @RequestBody @Valid MessageUpdateRequest request
    ) {
        MessageDto message = messageService.update(messageId, request);

        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    /**
     * 메시지 삭제
     */
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    @Operation(summary = "Message 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(examples = @ExampleObject("Message with id {messageId} not found")))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Message ID") @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }
}
