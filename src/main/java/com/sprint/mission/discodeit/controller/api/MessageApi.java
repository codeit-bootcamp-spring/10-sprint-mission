package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

  @Operation(summary = "Message 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Message 생성 성공",
          content = @Content(schema = @Schema(implementation = MessageDto.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found"))
      ),
  })
  ResponseEntity<MessageDto> create(
      @Parameter(
          description = "Message 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) MessageCreateRequest messageCreateRequest,
      @Parameter(
          description = "Message 첨부 파일들",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ) List<MultipartFile> attachments,
      @Parameter(hidden = true) @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );

  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Message 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = PageResponse.class))
      )
  })
  ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @Parameter(description = "조회할 Channel ID") UUID channelId,
      @Parameter(description = "페이징 커서 정보") Instant cursor,
      @Parameter(description = "페이징 정보", example = "{\"size\": 50, \"sort\": \"createdAt,desc\"}") Pageable pageable
  );

  @Operation(summary = "Message 내용 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Message 수정 성공",
          content = @Content(schema = @Schema(implementation = MessageDto.class))
      ),
      @ApiResponse(
          responseCode = "403",
          description = "수정 권한 없음 (본인이 작성하지 않은 메시지)",
          content = @Content(examples = @ExampleObject(value = "Only the author of the message can edit or delete it."))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
      ),
  })
  ResponseEntity<MessageDto> update(
      @Parameter(description = "수정할 Message ID") @PathVariable UUID messageId,
      @Parameter(description = "수정 요청 정보 (요청자 ID, 새로운 내용)") @RequestBody MessageUpdateRequest request,
      @Parameter(hidden = true) @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );

  @Operation(summary = "Message 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "Message 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "403",
          description = "삭제 권한 없음 (본인이 작성하지 않은 메시지)",
          content = @Content(examples = @ExampleObject(value = "Only the author of the message can edit or delete it."))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found"))
      ),
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Message ID") @PathVariable UUID messageId,
      @Parameter(hidden = true) @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );
}