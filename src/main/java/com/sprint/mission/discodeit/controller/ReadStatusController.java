package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ReadStatus")
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  // ReadStatus 생성
  @Operation(summary = "Message 읽음 상태 생성")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping
  public ResponseEntity<ReadStatusDto> createReadStatus(
      @RequestBody ReadStatusDto.ReadStatusCreateRequest createReq) {
    log.info("[Controller] ReadStatus 생성 요청: userId={}, channelId={}, lastReadAt={}",
        createReq.userId(), createReq.channelId(), createReq.lastReadAt());

    ReadStatusDto dto = readStatusService.createReadStatus(createReq);
    log.debug("[Controller] ReadStatus 생성 응답 준비: id={}", dto.id());

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // ReadStatus 조회
  @Operation(summary = "Message 읽음 상태 조회")
  @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
  @GetMapping("/{read-status-id}")
  public ResponseEntity<ReadStatusDto> findReadStatus(
      @PathVariable("read-status-id") UUID readStatusId) {
    ReadStatusDto dto = readStatusService.findById(readStatusId);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  // 특정 사용자별 ReadStatus 조회
  @Operation(summary = "User의 Message 읽음 상태 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
  @GetMapping(params = "userId")
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {
    List<ReadStatusDto> dto = readStatusService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  // ReadStatus 수정
  @Operation(summary = "Message 읽음 상태 수정")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PatchMapping("/{read-status-id}")
  public ResponseEntity<ReadStatusDto> updateReadStatus(
      @PathVariable("read-status-id") UUID readStatusId,
      @RequestBody ReadStatusDto.ReadStatusUpdateRequest updateReq) {
    log.info("[Controller] ReadStatus 수정 요청: id={}, newLastReadAt={}",
        readStatusId, updateReq.newLastReadAt());

    ReadStatusDto dto = readStatusService.updateReadStatus(readStatusId, updateReq);
    log.debug("[Controller] ReadStatus 수정 응답 준비: id={}", dto.id());

    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  // ReadStatus 삭제
  @Operation(summary = "Message 읽음 상태 삭제")
  @ApiResponse(responseCode = "204", description = "Message 읽음 상태가 성공적으로 삭제됨")
  @DeleteMapping("/{read-status-id}")
  public ResponseEntity<Void> deleteReadStatus(@PathVariable("read-status-id") UUID readStatusId) {
    log.info("[Controller] ReadStatus 삭제 요청: id={}", readStatusId);

    readStatusService.deleteReadStatusById(readStatusId);
    log.debug("[Controller] ReadStatus 삭제 응답 준비: id={}", readStatusId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
