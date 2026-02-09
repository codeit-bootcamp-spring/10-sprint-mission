package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final BasicReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ReadStatusResponse createStatus(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.createStatus(request);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public ReadStatusResponse updateStatus(@RequestBody ReadStatusUpdateRequest request) {
        return readStatusService.updateStatus(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<ReadStatusResponse> getReadStatuses(@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }
}