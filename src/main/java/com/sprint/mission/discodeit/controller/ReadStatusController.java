package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  public ReadStatusController(ReadStatusService readStatusService) {
    this.readStatusService = readStatusService;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ReadStatusResponse> create(@RequestBody ReadStatusCreateRequest dto) {
    UUID id = readStatusService.create(dto);
    return ResponseEntity.status(201).body(readStatusService.find(id));
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@RequestParam UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }

  @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
  public ResponseEntity<ReadStatusResponse> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest body
  ) {
    return ResponseEntity.ok(readStatusService.update(readStatusId, body));
  }
}