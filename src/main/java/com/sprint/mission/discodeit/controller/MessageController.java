package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.PageResponse;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.MessageService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message API")
public class MessageController {

  /*
   **메시지 관리**
  - [ ]  메시지를 보낼 수 있다.
  - [ ]  메시지를 수정할 수 있다.
  - [ ]  메시지를 삭제할 수 있다.
  - [ ]  특정 채널의 메시지 목록을 조회할 수 있다.
   */
  private final MessageService messageService;
  private final BinaryContentMapper binaryContentMapper;

  @Operation(summary = "Message 생성", operationId = "create_2")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨"),
      @ApiResponse(
          responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = """
                      {
                        "fieldErrors": null,
                        "violationErrors": null,
                        "code": 404,
                        "message": "존재하지 않는 채널"
                      }
                  """)
          )
      )
  })
  @Timed("message.create.async")
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<MessageDto> sendMessage(
      @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @RequestPart(value = "messageCreateRequest") @Valid MessageCreateRequest dto,
      @Parameter(description = "Message 첨부 파일들")
      @RequestPart(value = "attachments", required = false) List<MultipartFile> multipartFiles) {
    List<BinaryContentCreateDto> binaryContentCreateDtos = new ArrayList<>();
    if (multipartFiles != null) {
      for (MultipartFile multipartFile : multipartFiles) {
        binaryContentCreateDtos.add(binaryContentMapper.toCreateDto(multipartFile));
      }
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(messageService.create(dto, binaryContentCreateDtos));
  }

  @Operation(summary = "Message 내용 수정", operationId = "update_2",
      parameters = @Parameter(name = "messageId", description = "수정할 Message ID"))
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = """
                      {
                        "fieldErrors": null,
                        "violationErrors": null,
                        "code": 404,
                        "message": "존재하지 않는 메세지"
                      }
                  """)
          )
      )
  })
  @PatchMapping(path = "/{messageId}")
  public ResponseEntity<MessageDto> updateMessage(
      //@RequestHeader UUID userId,//인증/인가
      @PathVariable UUID messageId,
      @Valid @RequestBody MessageUpdateRequest dto) {
    return ResponseEntity.ok(messageService.update(messageId, dto));
  }

  @Operation(summary = "Message 삭제", operationId = "delete_1",
      parameters = @Parameter(name = "messageId", description = "삭제할 Message ID"))
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = """
                      {
                        "fieldErrors": null,
                        "violationErrors": null,
                        "code": 404,
                        "message": "존재하지 않는 메세지"
                      }
                  """)
          )
      )
  })
  @DeleteMapping(path = "/{messageId}")
  public ResponseEntity<Void> deleteMessage(
      //@RequestHeader UUID userId,//인증/인가 추가시
      @PathVariable UUID messageId
  ) {
    messageService.delete(messageId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Channel의 Message 목록 조회",
      parameters = {@Parameter(
          name = "channelId",
          description = "조회할 Channel ID",
          required = true),
          @Parameter(
              name = "pageable",
              description = "페이징 정보",
              example = """
                  {
                      "size": 50,
                      "sort": "createdAt,desc"
                  }
                  """
          ),
          @Parameter(
              name = "cursor",
              description = "페이징 커서 정보",
              required = false
          )
      }
  )
  @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findMessagesByChannelId(
      //@RequestHeader UUID userId,//나중에 인증/인가로
      @RequestParam("channelId") UUID channelId,
      @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
      @RequestParam(required = false) Instant cursor//널이면 가장 최근꺼 기준
  ) {
    /* 기존 오프셋 방식
    pageable = PageRequest.of(pageable.getPageNumber() == 0 ? 0 : pageable.getPageNumber() - 1,
        pageable.getPageSize(),
        pageable.getSort());

     */
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId, pageable, cursor));
  }

}
