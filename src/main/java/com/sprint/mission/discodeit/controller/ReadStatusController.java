package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusUpdateCommand;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @GetMapping
    public ResponseEntity<List<ReadStatusResponse>> find(@RequestParam UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(readStatusService.findAllByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ReadStatusResponse> create(@RequestBody @Valid ReadStatusCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(request));
    }

    @PatchMapping(value = "/{readStatusId}")
    public ResponseEntity<ReadStatusResponse> update(@PathVariable UUID readStatusId,
                                                     @RequestBody @Valid ReadStatusUpdateRequest request) {
        ReadStatusUpdateCommand command = new ReadStatusUpdateCommand(readStatusId, request.newLastReadAt());
        return ResponseEntity.status(HttpStatus.OK).body(readStatusService.update(command));
    }

}
