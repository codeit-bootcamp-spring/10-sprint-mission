package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 채널별 읽음 상태 관련 요청을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {
    private final ReadStatusService readStatusService;

    @Override
    public ResponseEntity<ReadStatusDto.Response> createReadStatus(ReadStatusDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(request));
    }

    @Override
    public ResponseEntity<ReadStatusDto.Response> updateReadStatus(UUID readStatusId, ReadStatusDto.UpdateRequest request) {
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }

    @Override
    public ResponseEntity<ReadStatusDto.Response> findReadStatus(UUID readStatusId) {
        return ResponseEntity.ok(readStatusService.find(readStatusId));
    }

    @Override
    public ResponseEntity<List<ReadStatusDto.Response>> findAllByUserId(UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}
