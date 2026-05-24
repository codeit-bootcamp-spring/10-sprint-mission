package com.sprint.mission.discodeit.readstatus.controller;

import com.sprint.mission.discodeit.readstatus.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.readstatus.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.readstatus.service.ReadStatusService;
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

@RestController
@RequestMapping("/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @GetMapping
  public ResponseEntity<List<ReadStatusResponse>> find(@RequestParam UUID userId) {
    return ResponseEntity.status(HttpStatus.OK).body(readStatusService.findAllByUserId(userId));
  }

  @PostMapping
  public ResponseEntity<ReadStatusResponse> create(
      @RequestBody @Valid ReadStatusCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(request));
  }

  @PatchMapping(value = "/{readStatusId}")
  public ResponseEntity<ReadStatusResponse> update(@PathVariable UUID readStatusId,
      @RequestBody @Valid ReadStatusUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(readStatusService.update(readStatusId, request));
  }

}
