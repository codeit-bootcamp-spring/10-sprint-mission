package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateMessageRequestDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UpdateMessageRequestDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Message")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "Message 생성", operationId = "create_2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Channel | Author with id {channelId | author} not found")))
    })
    @RequestMapping(method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto send(
            @RequestPart("messageCreateRequest") @Valid CreateMessageRequestDto request,
            @Parameter(description = "Message 첨부 파일들") @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) throws IOException {
        log.info("REST 요청 - Message 생성 시작");

        List<BinaryContentDto> attachmentDtos = new ArrayList<>();
        if(attachments != null && !attachments.isEmpty()){
            for(MultipartFile file : attachments){
                attachmentDtos.add(new BinaryContentDto(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getSize(),
                        file.getBytes()
                ));
            }
        }

            return messageService.create(request, attachmentDtos);
    }

    @Operation(summary = "Message 내용 수정", operationId = "update_2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
    })
    @RequestMapping(method = RequestMethod.PATCH, value = "/{messageId}")
    public ResponseEntity<MessageDto> update(@Parameter(description = "수정할 Message ID") @PathVariable UUID messageId,
                                             @Valid @RequestBody UpdateMessageRequestDto request){
        log.info("REST 요청 - Message 내용 수정 시작: messageId={}", messageId);
        return ResponseEntity.ok(messageService.update(messageId, request));
    }

    @Operation(summary = "Message 삭제", operationId = "delete_1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
    })
    @RequestMapping(method = RequestMethod.DELETE, value = "/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "삭제할 Message ID") @PathVariable UUID messageId){
        log.warn("REST 요청 - Message 삭제 시작: messageId={}", messageId);
        messageService.delete(messageId);
    }

    @Operation(summary = "Channel의 Message 목록 조회", operationId = "findAllByChannelId")
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannel(
            @Parameter(description = "조회할 Channel ID") @RequestParam UUID channelId,
            @Parameter(description = "페이징 커서 정보") @RequestParam(required = false) Instant cursor,
            @Parameter(description = "페이징 정보", example = "{\"size\": 50, \"sort\": \"createdAt,desc\"}")
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    )
    {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId, cursor, pageable));
    }
}