package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/read-status")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @Autowired
    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    // 특정 채널의 메시지 수신 정보를 생성할 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponse> create(@RequestBody ReadStatusCreateRequest request) {
        ReadStatusResponse response = readStatusService.create(request);
        return ResponseEntity.ok(response);
    }

    // 특정 채널의 메시지 수신 정보를 수정할 수 있다.
    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponse> update(@PathVariable UUID readStatusId) {
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(readStatusId);
        ReadStatusResponse response = readStatusService.update(request);
        return ResponseEntity.ok(response);
    }

    // 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@RequestParam UUID userId) {
        List<ReadStatusResponse> responses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(responses);
    }
}
