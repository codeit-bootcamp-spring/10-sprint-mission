package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequestDTO;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
@Slf4j
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createReadStatus(
            @RequestBody @Valid CreateReadStatusRequestDTO dto
    ) {
        log.debug("[READSTATUS_CREATE_REQUEST] 읽음 상태 생성 요청: userId={}, channelId={}", dto.userId(), dto.channelId());
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
            @RequestBody @Valid UpdateReadStatusRequestDTO dto
    ) {
        log.debug("[READSTATUS_UPDATE_REQUEST] 읽음 상태 수정 요청: readStatusId={}", readStatusId);
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
