package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @PostMapping
  public ResponseEntity<ReadStatusDto> create(
      @RequestBody ReadStatusCreateRequest request,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    // 권한 예외 처리 (500 에러 방지)
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    log.debug("Received POST /api/readStatuses request - userId: {}, channelId: {}",
        userDetails.getId(), request.channelId()); // 읽음 상태 생성 요청 로그

    ReadStatus readStatus = readStatusService.create(
        userDetails.getId(),
        request.channelId(),
        request.lastReadAt()
    );

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(readStatusMapper.toDto(readStatus));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    log.debug("Received GET /api/readStatuses request - userId: {}",
        userDetails.getId()); // 읽음 상태 조회 요청 로그

    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userDetails.getId());
    List<ReadStatusDto> dtos = readStatuses.stream()
        .map(readStatusMapper::toDto)
        .toList();

    return ResponseEntity.ok(dtos);
  }

  @Override
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusDto> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    log.debug("Received PATCH /api/readStatuses/{} request", readStatusId); // 읽음 상태 업데이트 요청 로그

    ReadStatus readStatus = readStatusService.update(
        readStatusId,
        request.newLastReadAt(),
        request.newNotificationEnabled()
    );

    return ResponseEntity.ok(readStatusMapper.toDto(readStatus));
  }
}
