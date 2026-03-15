package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @RequestMapping(method = RequestMethod.POST)
  @Operation(summary = "Message 읽음 상태 생성")
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨",
                  content = @Content(mediaType = "application.json",
                          schema = @Schema(implementation = ReadStatusDto.class)
                  )
          ),
          @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함",
                  content = @Content(
                          examples = @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists")
                  )
          ),
          @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found")
                  )
          )
  })
  public ResponseEntity<ReadStatus> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
    ReadStatus createdReadStatus = readStatusService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdReadStatus);
  }



  @RequestMapping(path = "/{readStatusId}", method = RequestMethod.PATCH)
  @Operation(summary = "Message 읽음 상태 수정")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨",
                  content = @Content(mediaType = "application.json",
                          schema = @Schema(implementation = ReadStatusDto.class)
                  )
          ),
          @ApiResponse(responseCode = "400", description = "Message 읽음 상태를 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found")
                  )
          )
  })
  public ResponseEntity<ReadStatus> updateReadStatus(@PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedReadStatus);
  }



  @RequestMapping(method = RequestMethod.GET)
  @Operation(summary = "User의 Message 읽음 상태 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
              content = @Content(mediaType = "application.json",
              schema = @Schema(implementation = ReadStatusDto.class)))
  public ResponseEntity<List<ReadStatus>> getReadStatus(@RequestParam UUID userId) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatuses);
  }
}
