package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.ErrorDto;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  @Operation(summary = "Message 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Message가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = MessageDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value = "{ \"status\": 404, \"error\": \"USER_NOT_FOUND\", "
                  + "\"message\": \"유저를 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @Parameter(
          description = "Message 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @Parameter(
          description = "Message 첨부 파일들",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ) @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> attachmentRequests =
        Optional.ofNullable(attachments)
            .map(files -> files.stream()
                .map(this::toBinaryContentCreateRequest)
                .toList())
            .orElse(List.of());

    MessageDto createdMessage =
        messageService.create(messageCreateRequest, attachmentRequests);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
  }

  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = MessageDto.class)))
      )
  })
  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @Parameter(description = "조회할 Channel ID") @RequestParam("channelId") UUID channelId,
      @Parameter(description = "페이징 커서 정보") @RequestParam(value = "cursor", required = false) Instant cursor,
      @Parameter(description = "페이징 정보")
      @ParameterObject @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    PageResponse<MessageDto> messages =
        messageService.findAllByChannelId(channelId, cursor, pageable);

    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }

  @Operation(summary = "Message 내용 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = MessageDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 404, \"error\": \"MESSAGE_NOT_FOUND\", "
                      + "\"message\": \"메시지를 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @PatchMapping(path = "/{messageId}")
  public ResponseEntity<MessageDto> update(
      @Parameter(description = "수정할 Message ID") @PathVariable UUID messageId,
      @Parameter(description = "수정할 Message 내용") @Valid @RequestBody MessageUpdateRequest request
  ) {

    MessageDto updatedMessage =
        messageService.update(messageId, request);

    return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
  }

  @Operation(summary = "Message 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "Message가 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 404, \"error\": \"MESSAGE_NOT_FOUND\", "
                      + "\"message\": \"메시지를 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @DeleteMapping(path = "/{messageId}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Message ID") @PathVariable UUID messageId
  ) {

    messageService.delete(messageId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private BinaryContentCreateRequest toBinaryContentCreateRequest(MultipartFile file) {

    String fileName = file.getOriginalFilename();
    String contentType = file.getContentType();

    if (fileName == null || fileName.isBlank()) {
      throw new IllegalArgumentException("파일 이름이 올바르지 않습니다: attachments");
    }

    if (contentType == null || contentType.isBlank()) {
      throw new IllegalArgumentException("파일 형식을 확인할 수 없습니다: attachments");
    }

    try {
      return new BinaryContentCreateRequest(
          fileName,
          contentType,
          file.getBytes()
      );
    } catch (IOException e) {
      throw new IllegalArgumentException("멀티파트 파일을 읽을 수 없습니다: attachments");
    }
  }
}