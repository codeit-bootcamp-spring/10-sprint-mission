package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @Operation(summary = "Message 읽음 상태 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = ReadStatusDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "이미 읽음 상태가 존재함",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 400, \"error\": \"READ_STATUS_ALREADY_EXISTS\", "
                      + "\"message\": \"메시지 읽음 상태가 이미 존재합니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      ),
      @ApiResponse(
          responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value = "{ \"status\": 404, \"error\": \"USER_NOT_FOUND\", "
                  + "\"message\": \"유저를 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @PostMapping
  public ResponseEntity<ReadStatusDto> create(
      @Parameter(description = "Message 읽음 상태 생성 정보") @Valid @RequestBody ReadStatusCreateRequest request) {
    ReadStatusDto createdReadStatus = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdReadStatus);
  }

  @Operation(summary = "User의 Message 읽음 상태 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatusDto.class)))
      )
  })
  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
      @Parameter(description = "조회할 User ID", required = true) @RequestParam("userId") UUID userId) {
    List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(readStatuses);
  }

  @Operation(summary = "Message 읽음 상태 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = ReadStatusDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 404, \"error\": \"READ_STATUS_NOT_FOUND\", "
                      + "\"message\": \"메시지 읽음 정보를 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @PatchMapping(path = "/{readStatusId}")
  public ResponseEntity<ReadStatusDto> update(
      @Parameter(description = "수정할 읽음 상태 ID") @PathVariable UUID readStatusId,
      @Parameter(description = "수정할 읽음 상태 정보") @Valid @RequestBody ReadStatusUpdateRequest request) {
    ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity.status(HttpStatus.OK).body(updatedReadStatus);
  }
}
