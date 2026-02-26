package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusPatchDto;
import com.sprint.mission.discodeit.dto.ReadStatusPostDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "ReadStatus controller 입니다.")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @RequestMapping(method = RequestMethod.GET)
  @Operation(summary = "User의 Message 읽음 상태 목록 조회", operationId = "findAllByUserId")
  public ResponseEntity<List<ReadStatusResponseDto>> findAllByUserId(
      @Parameter(name = "userId", description = "조회할 User ID") @RequestParam(value = "userId") UUID userId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(readStatusService.findAllByUserId(userId));
  }

  @RequestMapping(method = RequestMethod.POST)
  @Operation(summary = "Message 읽음 상태 생성", operationId = "create_1")
  public ResponseEntity<ReadStatusResponseDto> createReadStatus(
      @Valid @RequestBody ReadStatusPostDto readStatusPostDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(readStatusService.create(readStatusPostDto));
  }

  @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
  @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1")
  public ResponseEntity<ReadStatusResponseDto> updateReadStatus(
      @Parameter(name = "readStatusId", description = "수정할 읽음 상태 ID") @PathVariable UUID readStatusId,
      @Valid @RequestBody ReadStatusPatchDto readStatusPatchDto) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(readStatusService.update(readStatusId, readStatusPatchDto));
  }

}
