package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readstatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // ReadStatus 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto.response> createReadStatus(@RequestBody ReadStatusDto.createRequest createReq) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readStatusService.createReadStatus(createReq));
    }

    // ReadStatus 조회
    @RequestMapping(value = "/{read-status-id}", method = RequestMethod.GET)
    public ResponseEntity<ReadStatusDto.response> findReadStatus(@PathVariable("read-status-id") UUID readStatusId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(readStatusService.findById(readStatusId));
    }

    // 특정 사용자별 ReadStatus 조회
    @RequestMapping(value = "/findAll/{user-id}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto.response>> findAllByUserId(@PathVariable("user-id") UUID userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(readStatusService.findAllByUserId(userId));
    }

    // ReadStatus 수정
    @RequestMapping(value = "/{read-status-id}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusDto.response> updateReadStatus(@PathVariable("read-status-id") UUID readStatusId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(readStatusService.updateReadStatus(readStatusId, new ReadStatusDto.updateRequest(Instant.now())));
    }

    // ReadStatus 삭제
    @RequestMapping(value = "/{read-status-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteReadStatus(@PathVariable("read-status-id") UUID readStatusId) {
        readStatusService.deleteReadStatusById(readStatusId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
