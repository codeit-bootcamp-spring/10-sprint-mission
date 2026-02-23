package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@Tag(name = "ReadStatus")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @PostMapping
  @Operation(summary = "Message 읽음 상태 생성")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음"),
      @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함")
  })
  public ResponseEntity<?> create(@RequestBody ReadStatusDto.Create request) {
    ReadStatusDto.Response response = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{readStatusId}")
  @Operation(summary = "Message 읽음 상태 수정")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음")
  })
  public ResponseEntity<?> update(
      @Parameter(description = "수정할 읽음 상태 ID", example = "0d56555c-7d86-4fa7-b5d6-3170a70909e1")
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusDto.Update request) {
    ReadStatusDto.Response response = readStatusService.update(readStatusId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping
  @Operation(summary = "User의 Message 읽음 상태 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
  public ResponseEntity<?> findAllByUserId(
      @Parameter(description = "조회할 User ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
      @RequestParam("userId") UUID userId
  ) {
    List<ReadStatusDto.Response> responses = readStatusService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }
}
