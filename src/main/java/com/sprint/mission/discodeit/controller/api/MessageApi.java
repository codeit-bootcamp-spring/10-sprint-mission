package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "메시지 관리 API")
public interface MessageApi {

    @Operation(summary = "메시지 생성", description = "텍스트 내용과 여러 개의 첨부 파일을 포함한 메시지를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "404", description = "채널 또는 작성자를 찾을 수 없음")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = @Encoding(name = "messageCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
            )
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MessageDto.Response> createMessage(
            @Parameter(description = "메시지 생성 정보") @RequestPart("messageCreateRequest") @Valid MessageDto.CreateRequest request,
            @Parameter(description = "첨부 파일 목록") @RequestPart(value = "attachments", required = false) List<MultipartFile> files
    );

    @Operation(summary = "메시지 수정", description = "메시지의 텍스트 내용을 수정합니다.")
    @PatchMapping("/{messageId}")
    ResponseEntity<MessageDto.Response> updateMessage(
            @Parameter(description = "수정할 메시지 ID") @PathVariable("messageId") UUID messageId,
            @RequestBody @Valid MessageDto.UpdateRequest request
    );

    @Operation(summary = "메시지 단건 조회")
    @GetMapping("/{messageId}")
    ResponseEntity<MessageDto.Response> findMessage(
            @Parameter(description = "조회할 메시지 ID") @PathVariable("messageId") UUID messageId
    );

    @Operation(summary = "메시지 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{messageId}")
    ResponseEntity<Void> deleteMessage(
            @Parameter(description = "삭제할 메시지 ID") @PathVariable("messageId") UUID messageId
    );

    @Operation(summary = "채널 메시지 목록 조회", description = "커서 기반 페이징을 사용하여 특정 채널의 메시지를 조회합니다.")
    @GetMapping
    ResponseEntity<PageResponse<MessageDto.Response>> findAllByChannelId(
            @Parameter(description = "조회할 채널 ID") @RequestParam("channelId") UUID channelId,
            @Parameter(description = "커서 (이전 응답의 nextCursor 값)") @RequestParam(value = "cursor", required = false) Instant cursor,
            @ParameterObject Pageable pageable
    );
}
