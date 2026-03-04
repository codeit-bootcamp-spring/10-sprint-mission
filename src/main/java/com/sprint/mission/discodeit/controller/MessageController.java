package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    public ResponseEntity<MessageDto.messageResponse> createMessage(@RequestPart("messageCreateRequest") MessageDto.messageCreateRequest messageReq,
                                                                    @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) throws IOException {
        List<BinaryContentDto.binaryContentCreateRequest> contentReqs = toServiceDto(attachments);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.createMessage(messageReq, contentReqs));
    }

    // 메시지 단일 조회(UUID)
//    @RequestMapping(value = "/{message-id}", method = RequestMethod.GET)
//    public ResponseEntity<MessageDto.messageResponse> findMessage(@PathVariable("message-id") UUID messageId) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(messageService.findMessage(messageId));
//    }

    // 메시지 채널 조회(UUID)
    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
    @RequestMapping(params = "channelId", method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto.messageResponse>> findAllByChannelId(@RequestParam UUID channelId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(messageService.findAllByChannelId(channelId));
    }

    // 메시지 수정
    @Operation(summary = "Message 내용 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{message-id}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageDto.messageResponse> updateMessage(@PathVariable("message-id") UUID messageId,
                                                                    @RequestBody MessageDto.messageUpdateRequest messageReq) {
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
    public ResponseEntity<Void> deleteMessage(@PathVariable("message-id") UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private List<BinaryContentDto.binaryContentCreateRequest> toServiceDto(List<MultipartFile> attachments) throws IOException {
        if (attachments == null) return null;

        List<BinaryContentDto.binaryContentCreateRequest> results = new ArrayList<>();
        for (MultipartFile attachment : attachments) {
            results.add(new BinaryContentDto.binaryContentCreateRequest(BinaryContentType.fromMimeType(attachment.getContentType()),
                    attachment.getOriginalFilename(), attachment.getBytes()));
        }

        return results;
    }
}
