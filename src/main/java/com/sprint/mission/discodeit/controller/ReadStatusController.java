package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusResponse> createReadStatus(@RequestBody ReadStatusCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReadStatusResponse> updateReadStatus(
            @PathVariable UUID id,
            @RequestBody ReadStatusUpdateRequest request){
        return ResponseEntity.ok(readStatusService.update(id, request));
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@RequestParam UUID userId){
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}
