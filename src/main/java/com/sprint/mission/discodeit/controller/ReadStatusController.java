package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping(value = "/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  public ReadStatusController(ReadStatusService readStatusService) {
    this.readStatusService = readStatusService;
  }

  @Operation(summary = "Message 읽음 상태 생성", operationId = "create_1", tags = {"ReadStatus"})
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨"),
      @ApiResponse(
          responseCode = "400",
          description = "이미 읽음 상태가 존재함",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(
                  value = "ReadStatus with userId {userId} and channelId {channelId} already exists"
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found")
          )
      )
  })
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ReadStatusDto> create(@Valid @RequestBody ReadStatusCreateRequest dto) {
    UUID id = readStatusService.create(dto);
    return ResponseEntity.status(201).body(toDto(readStatusService.find(id)));
  }

  @Operation(summary = "User의 Message 읽음 상태 목록 조회", operationId = "findAllByUserId", tags = {
      "ReadStatus"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
  })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
      @Parameter(description = "조회할 User ID")
      @RequestParam UUID userId
  ) {
    return ResponseEntity.ok(
        readStatusService.findAllByUserId(userId).stream()
            .map(this::toDto)
            .toList()
    );
  }

  @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1", tags = {"ReadStatus"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨"),
      @ApiResponse(
          responseCode = "404",
          description = "Message 읽음 상태를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found")
          )
      )
  })
  @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
  public ResponseEntity<ReadStatusDto> update(
      @Parameter(description = "수정할 읽음 상태 ID")
      @PathVariable UUID readStatusId,
      @Valid @RequestBody ReadStatusUpdateRequest body
  ) {
    return ResponseEntity.ok(toDto(readStatusService.update(readStatusId, body)));
  }

  private ReadStatusDto toDto(ReadStatusResponse response) {
    return new ReadStatusDto(
        response.id(),
        response.userId(),
        response.channelId(),
        response.readAt()
    );
  }
}