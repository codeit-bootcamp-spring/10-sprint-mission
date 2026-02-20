package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@Tag(name = "ReadStatus")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @PostMapping
  @Operation(summary = "Message 읽음 상태 생성")
  public ResponseEntity<?> create(@RequestBody ReadStatusDto.Create request) {
    ReadStatusDto.Response response = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{readStatusId}")
  @Operation(summary = "Message 읽음 상태 수정")
  public ResponseEntity<?> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusDto.Update request) {
    ReadStatusDto.Response response = readStatusService.update(readStatusId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping
  @Operation(summary = "User의 Message 읽음 상태 목록 조회")
  public ResponseEntity<?> findAllByUserId(@RequestParam UUID userId) {
    List<ReadStatusDto.Response> responses = readStatusService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }
}
