package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @GetMapping
  public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(
      @RequestParam UUID userId
  ) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }

  @PostMapping
  public ResponseEntity<UUID> create(
      @Valid @RequestBody ReadStatusCreateRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(readStatusService.create(request));
  }

  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusResponse> update(
      @PathVariable UUID readStatusId,
      @Valid @RequestBody ReadStatusUpdateRequest request
  ) {
    return ResponseEntity.ok(readStatusService.update(readStatusId, request));
  }

  @DeleteMapping("/{readStatusId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID readStatusId
  ) {
    readStatusService.delete(readStatusId);
    return ResponseEntity.noContent().build();
  }
}