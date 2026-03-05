package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequestDTO;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createReadStatus(
            @RequestBody CreateReadStatusRequestDTO dto
    ) {
        ReadStatusDto created = readStatusService.createReadStatus(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location)
                .body(created);
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity updateReadStatus(
            @PathVariable UUID readStatusId,
            @RequestBody UpdateReadStatusRequestDTO dto
    ) {
        ReadStatusDto updated = readStatusService.updateReadStatus(readStatusId, dto);

        return ResponseEntity.ok(updated);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getReadStatus(
            @RequestParam UUID userId
    ) {
        List<ReadStatusDto> statuses = readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(statuses);
    }
}
