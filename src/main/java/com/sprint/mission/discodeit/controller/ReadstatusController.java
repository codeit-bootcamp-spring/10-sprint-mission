package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readstatus")
public class ReadstatusController {

    private final ReadStatusService readStatusService;

    // 생성  userId, ChannelId
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponse> create(@RequestBody ReadStatusCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(request));
    }

    //readStatusId , readNow 필요
    @RequestMapping(value = "/{readStatusId}", method= RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponse> update(@PathVariable UUID readStatusId,
                                                     @RequestBody ReadStatusUpdateRequest body){
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(
                readStatusId,
                body.readNow()
        );
        ReadStatusResponse response = readStatusService.update(request);
        return ResponseEntity.ok(response);
    }

    // 특정 유저의 메세지 수신 정보 확인!!
    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}
