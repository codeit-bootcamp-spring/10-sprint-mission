package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
public class MessageController {

  private final MessageService messageService;

  @RequestMapping(method = RequestMethod.POST)
  @Operation(summary = "Message 생성")
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨",
                  content = @Content(mediaType = "application.json",
                          schema = @Schema(implementation = MessageDto.class)
                  )
          ),
          @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found")
                  )
          )
  })
  public ResponseEntity<Message> createMessage(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try {
                return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
                );
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
            .toList())
        .orElse(new ArrayList<>());
    Message createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  @RequestMapping(path = "/{messageId}", method = RequestMethod.PATCH)
  @Operation(summary = "Message 내용 수정")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨",
                  content = @Content(mediaType = "application.json",
                          schema = @Schema(implementation = MessageDto.class)
                  )
          ),
          @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "Message with id {messageId} not found")
                  )
          )
  })
  public ResponseEntity<Message> update(@PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    Message updatedMessage = messageService.update(messageId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  @RequestMapping(path = "/{messageId}", method = RequestMethod.DELETE)
  @Operation(summary = "Message 삭제")
  @ApiResponses({
          @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "Message with id {messageId} not found")
                  )
          )
  })
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @RequestMapping(method = RequestMethod.GET)
  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공",
          content = @Content(mediaType = "application.json",
                  schema = @Schema(implementation = MessageDto.class)))
  public ResponseEntity<List<Message>> findAllByChannelId(
                                                  @RequestParam("channelId") UUID channelId) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }
}
