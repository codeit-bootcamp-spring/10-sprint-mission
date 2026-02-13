package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/{channelId}/read-status", method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto> create(@PathVariable UUID channelId,
                                                @RequestHeader UUID userId){
        ReadStatusDto response = readStatusService.create(channelId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(value = "/read-status", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestHeader UUID userId){
        List<ReadStatusDto> responses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @RequestMapping(value = "/{channelId}/read-status", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusDto> update(@PathVariable UUID channelId,
                                                @RequestHeader UUID userId) {
        ReadStatusDto response = readStatusService.update(channelId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
