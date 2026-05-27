package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    /**
     * 특정 채널 메시지 수신 정보 생성
     * 채널 생성/참여 시 자동으로 생성되게 이미 설정되어 있음
     */
    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함", content = @Content(examples = @ExampleObject(value = "ReadStatus with id {id} and channelId {channelId} already exists"))),
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Channel | User with id {channelId | id} not found")))
    })
    public ResponseEntity<ReadStatusDto> create(
            @RequestBody @Valid ReadStatusCreateRequest request
    ) {
        ReadStatusDto readStatus = readStatusService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    /**
     * 특정 채널의 메시지 수신 정보 수정
     */
    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    @Operation(summary = "Message 읽음 상태 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found")))
    })
    public ResponseEntity<ReadStatusDto> update(
            @Parameter(description = "수정할 읽음 상태 ID") @PathVariable UUID readStatusId,
            @RequestBody @Valid ReadStatusUpdateRequest request
            ) {
        ReadStatusDto readStatus = readStatusService.update(readStatusId, request);

        return ResponseEntity.status(HttpStatus.OK).body(readStatus);
    }

    /**
     * 특정 사용자의 메시지 수신 정보 조회
     */
    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
            @Parameter(description = "조회할 User ID") @RequestParam UUID userId
    ) {
        List<ReadStatusDto> result = readStatusService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
