package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatus;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
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
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @PostMapping
  public ResponseEntity<ReadStatus> create(
      @RequestBody ReadStatusCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(readStatusService.create(request));
  }

  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    return ResponseEntity.ok(readStatusService.update(readStatusId, request));
  }

  @GetMapping
  public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }
}
