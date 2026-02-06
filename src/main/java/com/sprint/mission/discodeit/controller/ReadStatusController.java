package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(method=RequestMethod.PATCH)
    public ReadStatusResponse updateReadStatus(@RequestBody ReadStatusUpdateRequest request){
        return readStatusService.update(request);
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(value="/{user-id}", method=RequestMethod.GET)
    public List<ReadStatusResponse> getReadStatusByUserId(@RequestBody UUID userID){
        return readStatusService.findAllByUserID(userID);
    }
}
