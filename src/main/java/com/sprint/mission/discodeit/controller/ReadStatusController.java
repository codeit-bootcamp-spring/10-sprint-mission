package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/read-status")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ReadStatus createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public ReadStatus updateReadStatus(@PathVariable UUID id,
                                       @RequestBody ReadStatusUpdateRequest request) {

        return readStatusService.update(id, request);

    }
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ReadStatus> getAllReadStatus(@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }




}
