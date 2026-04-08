package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ReadStatus")
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final BasicReadStatusService readStatusService;

  @Operation(summary = "Message 읽음 상태 생성", operationId = "create_1")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨",
          content = @Content(
              schema = @Schema(implementation = ReadStatusDto.class))),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(
                  value = "Channel | User with id {channelId | userId} not found"))),
      @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함",
          content = @Content(
              examples = @ExampleObject(
                  value = "ReadStatus with userId {userId} and channelId {channelId} already exists")))
  })
  @PostMapping
  public ResponseEntity<ReadStatusDto> createStatus(@RequestBody ReadStatusCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.createStatus(request));
  }

  @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨",
          content = @Content(
              schema = @Schema(implementation = ReadStatusDto.class))),
      @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(
                  value = "ReadStatus with id {readStatusId} not found")))
  })
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusDto> updateStatus(
      @Parameter(description = "수정할 읽음 상태 ID", required = true, schema = @Schema(type = "string", format = "uuid"))
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    return ResponseEntity.ok(readStatusService.updateStatus(readStatusId, request));
  }

  @Operation(summary = "User의 Message 읽음 상태 목록 조회", operationId = "findAllByUserId")
  @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
      content = @Content(schema = @Schema(implementation = ReadStatusDto.class)))
  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> getReadStatuses(
      @Parameter(description = "조회할 User ID", required = true, schema = @Schema(type = "string", format = "uuid")) @RequestParam UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }
}