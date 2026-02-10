package com.sprint.mission.discodeit.contorller;


import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/read-statuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UUID> createReadStatus(
            @RequestBody CreateReadStatusRequest request
    ) {
        UUID id = readStatusService.createReadStatus(request);

        return ResponseEntity.ok(id);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponse> updateReadStatus(
            @RequestBody UpdateReadStatusRequest request
    ) {
        ReadStatusResponse response =
                readStatusService.updateReadStatus(request);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(
            @PathVariable UUID userId
    ) {
        List<ReadStatusResponse> responses =
                readStatusService.findAllReadStatusesByUserId(userId);

        return ResponseEntity.ok(responses);
    }
}
