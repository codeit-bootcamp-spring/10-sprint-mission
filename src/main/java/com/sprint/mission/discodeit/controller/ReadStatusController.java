package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @PostMapping
  public ResponseEntity<ReadStatusDto> create(
      @RequestBody ReadStatusCreateRequest request) {
    ReadStatus readStatus = readStatusService.create(request.userId(), request.channelId());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(readStatusMapper.toDto(readStatus));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
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
    ReadStatus readStatus = readStatusService.update(readStatusId, request.newLastReadAt());

    return ResponseEntity.ok(readStatusMapper.toDto(readStatus));
  }
}
