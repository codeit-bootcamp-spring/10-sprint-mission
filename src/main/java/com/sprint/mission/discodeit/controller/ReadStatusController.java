package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // 특정 채널 메시지 수신 정보 생성
    @PostMapping
    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Message 읽음 상태가 성공적으로 생성됨",
                    content = @Content(
                            schema = @Schema(implementation = ReadStatusDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 읽음 상태가 존재함",
                    content = @Content(
                            examples = @ExampleObject(value = "이미 channelId:{channelId}, userId:{userId}와 관련된 객체가 존재합니다")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "{channelId | userId}를 가진 채널 | 유저는 존재하지 않습니다")
                    )
            )
    })
    public ResponseEntity<ReadStatusDto> create(@RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatusDto response = readStatusService.create(readStatusCreateRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 특정 채널 메시지 수신 정보를 수정
    @PatchMapping(value = "/{readStatusId}")
    @Operation(summary = "Message 읽음 상태 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태가 성공적으로 수정됨",
                    content = @Content(
                            schema = @Schema(implementation = ReadStatusDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "readStatusId:{readStatusId}를 가진 ReadStatus를 찾지 못했습니다")
                    )
            )
    })
    public ResponseEntity<ReadStatusDto> update(
            @Parameter(description = "수정할 읽음 상태 ID")
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatusDto response = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 특정 사용자의 메시지 수신 정보를 조회
    @GetMapping
    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태 목록 조회 성공",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ReadStatusDto.class)
                            )
                    )
            )
    })
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
            @Parameter(description = "조회할 User ID")
            @RequestParam UUID userId) {
        List<ReadStatusDto> response = readStatusService.findAllByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
