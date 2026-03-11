package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/messages")
public class MessageController {

    private final MessageService messageService;


    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
        @ApiResponse(
            responseCode = "404",
            description = "Channel 또는 User를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject("Channel | Author with id {channelId | authorId} not found")
            )
        ),
        @ApiResponse(
            responseCode = "201",
            description = "Message가 성공적으로 생성됨",
            content = @Content(
                schema = @Schema(implementation = MessageDto.class)
            )
        )
    })
    public ResponseEntity<MessageDto> createMessage(
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
        @RequestPart("messageCreateRequest") MessageCreateRequestDTO req) {
        return new ResponseEntity<>(messageService.create(attachments, req), HttpStatus.CREATED);

    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Message가 성공적으로 수정됨",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Message를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject("Message with id {messageId} not found")
            )
        )
    })
    public MessageDto editMessage(@PathVariable UUID messageId,
        @RequestBody MessageUpdateRequestDto req) {
        return messageService.update(messageId, req);
    }


    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Message가 성공적으로 삭제됨"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Message를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject("Message with id {messageId} not found")
            )
        )
    })
    public ResponseEntity<Void> deleteMessage(
        @PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }


    @RequestMapping(method = RequestMethod.GET)
    @ApiResponse(
        responseCode = "200",
        description = "Message 목록 조회 성공",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = PageResponse.class))
        )
    )
    public ResponseEntity<PageResponse<MessageDto>> viewChannelMessage(
        @RequestParam UUID channelId,
        @RequestParam(required = false) Optional<Instant> cursor,
        @ParameterObject
        @PageableDefault(page = 0, size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
            messageService.findAllByChannelId(
                channelId,
                cursor,
                pageable));
    }
}
