package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
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


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto.Response> createReadStatus(
            @RequestBody ReadStatusDto.CreateRequest request) {
        ReadStatusDto.Response response = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<ReadStatusDto.Response> findReadStatus(@RequestParam("read-status-id") UUID readStatusId) {
        ReadStatusDto.Response response = readStatusService.find(readStatusId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user-id}")
    public ResponseEntity<List<ReadStatusDto.Response>> findAllByUserId(@PathVariable("user-id") UUID userId) {
        List<ReadStatusDto.Response> response = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
