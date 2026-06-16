package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "채널 읽음 상태 관리 API")
public interface ReadStatusApi {

    @Operation(summary = "읽음 상태 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "이미 존재함")
    })
    @PostMapping
    ResponseEntity<ReadStatusDto.Response> createReadStatus(
            @RequestBody @Valid ReadStatusDto.CreateRequest request
    );

    @Operation(summary = "읽음 상태 수정")
    @PatchMapping("/{readStatusId}")
    ResponseEntity<ReadStatusDto.Response> updateReadStatus(
            @Parameter(description = "수정할 읽음 상태 ID") @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody @Valid ReadStatusDto.UpdateRequest request
    );

    @Operation(summary = "읽음 상태 단건 조회")
    @GetMapping("/{readStatusId}")
    ResponseEntity<ReadStatusDto.Response> findReadStatus(
            @Parameter(description = "조회할 읽음 상태 ID") @PathVariable("readStatusId") UUID readStatusId
    );

    @Operation(summary = "사용자별 읽음 상태 목록 조회")
    @GetMapping
    ResponseEntity<List<ReadStatusDto.Response>> findAllByUserId(
            @Parameter(description = "사용자 ID") @RequestParam("userId") UUID userId
    );
}
