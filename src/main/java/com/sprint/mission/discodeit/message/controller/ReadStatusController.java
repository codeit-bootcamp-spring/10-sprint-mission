package com.sprint.mission.discodeit.message.controller;

import com.sprint.mission.discodeit.message.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.message.dto.ReadStatusDto;
import com.sprint.mission.discodeit.message.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.message.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus")
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @Operation(summary = "Message 읽음 상태 생성",
      operationId = "create_1")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Message 읽음 상태가 성공적으로 생성됨"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "이미 읽음 상태가 존재함",
          content = @Content(
              examples = @ExampleObject(
                  value = "ReadStatus with userId {userId} and channelId {channelId} already exists")
          )

      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(
                  value = "Channel | User with id {channelId | userId} not found")
          )
      )
  })
  @PostMapping
  public ResponseEntity<ReadStatusDto> createReadStatus(
      @Parameter(description = "Message 읽음 상태 생성 정보")
      @RequestBody ReadStatusCreateRequest request) {
    ReadStatusDto readStatus = readStatusService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(readStatus);
  }

  @Operation(summary = "Message 읽음 상태 수정",
      operationId = "update_1")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Message 읽음 상태가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404",
          description = "Message 읽음 상태를 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(
                  value = "ReadStatus with id {readStatusId} not found"
              )
          ))
  }
  )
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusDto> updateReadStatus
      (@Parameter(description = "수정할 읽음 상태 ID")
          @PathVariable UUID readStatusId,
          @Parameter(description = "수정할 읽음 상태 정보")
          @RequestBody ReadStatusUpdateRequest request) {
    ReadStatusDto readStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity.
        status(HttpStatus.OK)
        .body(readStatus);
  }

  @Operation(summary = "User의 Message 읽음 상태 목록 조회")
  @ApiResponses(
      @ApiResponse(responseCode = "200",
          description = "Message 읽음 상태 목록 조회 성공")
  )
  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
      @Parameter(description = "조회할 User ID") @RequestParam UUID userId) {
    List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatuses);
  }

  @GetMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusDto> findById(@PathVariable UUID readStatusId) {
    ReadStatusDto readStatus = readStatusService.find(readStatusId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatus);
  }
}
