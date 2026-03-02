package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping
    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함")
    })
    public ResponseEntity<ReadStatus> createReadStatus(
            @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatus response = readStatusService.createReadStatus(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{readStatusId}")
    @Operation(summary = "Message 읽음 상태 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음")
    })
    public ResponseEntity<ReadStatus> updateReadStatus(
            @Parameter(description = "수정할 읽음 상태 ID", example = "0f9238f3-cd81-4f82-8611-3a22b16da66d")
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatus response = readStatusService.updateReadStatus(readStatusId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @Parameter(description = "조회할 User ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
            @RequestParam UUID userId
    ) {
        List<ReadStatus> responses = readStatusService.findAllReadStatusesByUserId(userId);

        return ResponseEntity.ok(responses);
    }
}
