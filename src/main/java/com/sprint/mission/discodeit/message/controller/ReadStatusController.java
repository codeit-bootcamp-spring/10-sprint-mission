package com.sprint.mission.discodeit.message.controller;

import com.sprint.mission.discodeit.message.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.message.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.message.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.message.entity.ReadStatus;
import com.sprint.mission.discodeit.message.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messagereadstatuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ReadStatusResponse createReadStatus (@RequestBody ReadStatusCreateRequest request){
        return readStatusService.create(request);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ReadStatusResponse updateReadStatus (@RequestBody ReadStatusUpdateRequest request) {
        return readStatusService.update(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatusResponse> getByUserId(@RequestParam(name = "users") UUID id) {
        return readStatusService.findAllByUserId(id);
    }
}
