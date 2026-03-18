package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
@Tag(name = "ReadStatus")
@RequestMapping("/api/readStatuses")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // ReadStatus 생성
    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto> createReadStatus(@RequestBody ReadStatusDto.ReadStatusCreateRequest createReq) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readStatusService.createReadStatus(createReq));
    }

    // ReadStatus 조회
    @Operation(summary = "Message 읽음 상태 조회")
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
    @RequestMapping(value = "/{read-status-id}", method = RequestMethod.GET)
    public ResponseEntity<ReadStatusDto> findReadStatus(@PathVariable("read-status-id") UUID readStatusId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(readStatusService.findById(readStatusId));
    }

    // 특정 사용자별 ReadStatus 조회
    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
    @RequestMapping(params = "userId", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(readStatusService.findAllByUserId(userId));
    }

    // ReadStatus 수정
    @Operation(summary = "Message 읽음 상태 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{read-status-id}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusDto> updateReadStatus(@PathVariable("read-status-id") UUID readStatusId,
                                                          @RequestBody ReadStatusDto.ReadStatusUpdateRequest updateReq) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(readStatusService.updateReadStatus(readStatusId, updateReq));
    }

    // ReadStatus 삭제
    @Operation(summary = "Message 읽음 상태 삭제")
    @ApiResponse(responseCode = "204", description = "Message 읽음 상태가 성공적으로 삭제됨")
    @RequestMapping(value = "/{read-status-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteReadStatus(@PathVariable("read-status-id") UUID readStatusId) {
        readStatusService.deleteReadStatusById(readStatusId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
