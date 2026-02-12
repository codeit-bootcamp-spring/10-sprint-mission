package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CreateReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequestDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/read-statuses")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ReadStatusDto create(@Valid @RequestBody CreateReadStatusRequestDto request){
        return readStatusService.create(request);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{readStatusId}")
    public ReadStatusDto update(@PathVariable UUID readStatusId,
                                @Valid @RequestBody UpdateReadStatusRequestDto request){
        return readStatusService.update(readStatusId, request);
    }

    // 단건 조회
    @RequestMapping(method = RequestMethod.GET, value = "/{readStatusId}")
    public ReadStatusDto find(@PathVariable UUID readStatusId){
        return readStatusService.find(readStatusId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatusDto> findAllByUser(@RequestParam UUID userId){
        return readStatusService.findAllByUserId(userId);
    }
}
