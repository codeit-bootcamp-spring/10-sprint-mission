package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/readstatus")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    //특정 채널의 메시지 수신 정보 생성
    @RequestMapping(method = RequestMethod.POST)
    public ReadStatusResponse postReadStatus(@RequestBody ReadStatusCreateRequest dto) {
        UUID readStatusId = readStatusService.create(dto);
        return readStatusService.find(readStatusId);
    }

    // 특정 채널의 메시지 수신 정보 수정
    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PUT)
    public ReadStatusResponse putReadStatus(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest dto
    ) {
        if (dto == null || dto.readStatus() == null) {
            throw new IllegalArgumentException("readStatusId null이 될 수 없습니다.");
        }
        if (!readStatusId.equals(dto.readStatus())) {
            throw new IllegalArgumentException("path readStatusId와 body readStatusId가 일치해야 합니다.");
        }
        return readStatusService.update(dto);
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatusResponse> getReadStatusByUserId(@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }
}

