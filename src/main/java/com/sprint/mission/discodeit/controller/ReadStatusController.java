package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusDto> create(
            @Valid @RequestBody ReadStatusCreateRequest request) {
        log.debug("[READ_STATUS_CREATE] 읽음 상태 생성 요청: userId={}, channelId={}",
                request.userId(), request.channelId());

        ReadStatusDto createdReadStatus = readStatusService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdReadStatus);
    }

    @PatchMapping(path = "/{readStatusId}")
    public ResponseEntity<ReadStatusDto> update(@PathVariable("readStatusId") UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request) {
        log.debug("[READ_STATUS_UPDATE] 읽음 상태 수정 요청: readStatusId={}", readStatusId);

        ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedReadStatus);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
            @RequestParam("userId") UUID userId) {
        log.debug("[READ_STATUS_FIND_ALL] 읽음 상태 목록 조회 요청: userId={}", userId);

        List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readStatuses);
    }
}
