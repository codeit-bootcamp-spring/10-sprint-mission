package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;
    private final ReadStatusMapper readStatusMapper;

    @Operation(summary = "읽음상태 생성")
    @ApiResponse(
            responseCode = "201",
            description = "readStatus created"
    )
    @PostMapping
    public ResponseEntity<ReadStatusDto> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        ReadStatus readStatus = readStatusService.create(request);
        ReadStatusDto readStatusDto = readStatusMapper.toDto(readStatus);

        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusDto);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> getAllReadStatus(@RequestParam UUID userId) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);

        List<ReadStatusDto> readStatusDtos = readStatuses.stream().map(readStatusMapper::toDto).toList();

        return ResponseEntity.ok(readStatusDtos);
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> updateReadStatus(@PathVariable UUID readStatusId,
                                       @RequestBody ReadStatusUpdateRequest request) {

        ReadStatus readStatus = readStatusService.update(readStatusId, request);
        ReadStatusDto readStatusDto = readStatusMapper.toDto(readStatus);

        return ResponseEntity.ok(readStatusDto);

    }





}
