package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatus")
@RequiredArgsConstructor
public class ReadStatusController {
    public final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성
    @PostMapping
    public ResponseEntity<ReadStatus> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        return ResponseEntity.ok(readStatusService.create(request));
    }

    // 특정 채널의 메시지 수신 정보 수정
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatus> updateReadStatus(@PathVariable UUID readStatusId,
                                                       @RequestBody ReadStatusUpdateRequest request) {
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @GetMapping
    public ResponseEntity<List<ReadStatus>> getReadStatusesByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

}
