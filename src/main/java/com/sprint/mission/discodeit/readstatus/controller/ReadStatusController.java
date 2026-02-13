package com.sprint.mission.discodeit.readstatus.controller;

import com.sprint.mission.discodeit.readstatus.dto.ReadStatusCreateInfo;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusInfo;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusUpdateInfo;
import com.sprint.mission.discodeit.readstatus.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/readstatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<ReadStatusInfo> createReadStatus(@RequestBody ReadStatusCreateInfo statusInfo) {
        return ResponseEntity.ok(readStatusService.createReadStatus(statusInfo));
    }

    @RequestMapping(value = "/{statusId}", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateReadStatus(@PathVariable UUID statusId) {
        readStatusService.updateReadStatus(statusId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateReadStatus(ReadStatusUpdateInfo updateInfo) {
        readStatusService.updateReadStatus(updateInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusInfo>> getReadStatuses(@PathVariable UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    @RequestMapping(value = "/{statusId}", method = RequestMethod.GET)
    public ResponseEntity<ReadStatusInfo> getReadStatus(@PathVariable UUID statusId) {
        return ResponseEntity.ok(readStatusService.find(statusId));
    }
}
