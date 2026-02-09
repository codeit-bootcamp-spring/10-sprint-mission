package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ReadStatusDto.Response create(@RequestBody ReadStatusDto.CreateRequest request) {
        return readStatusService.create(request);
    }

    // TODO: 경로의 id와 request body의 id가 일치하는지 검증 로직 추가 예정
    @RequestMapping(method = RequestMethod.PATCH)
    public ReadStatusDto.Response update(@RequestBody ReadStatusDto.UpdateRequest request) {
        return readStatusService.update(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatusDto.Response> findAllByUserId(@RequestParam("userId")UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }
}
