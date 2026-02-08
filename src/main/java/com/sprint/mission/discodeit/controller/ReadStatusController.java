package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( "/read-status")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    // ReadStatus 정보 생성
    @RequestMapping(method= RequestMethod.POST)
    public ReadStatusResponse postReadStatus(@RequestBody ReadStatusCreateRequest request){
        return readStatusService.create(request);
    }

    // ReadStatus 정보 수정
    @RequestMapping(value="/{readStatus-id}" ,method=RequestMethod.PATCH)
    public ReadStatusResponse updateReadStatus(@PathVariable("readStatus-id") UUID readStatusId,
                                               @RequestBody ReadStatusUpdateRequest request){
        return readStatusService.update(readStatusId, request);
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(value="/user", method=RequestMethod.GET)
    public List<ReadStatusResponse> getReadStatusByUserId(@RequestParam("userId") UUID userId){
        return readStatusService.findAllByUserID(userId);
    }
}
