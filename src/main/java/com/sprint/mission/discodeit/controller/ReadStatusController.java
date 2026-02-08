package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readstatus")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;
    /*
    ### **메시지 수신 정보 관리**
    - [ ]  특정 채널의 메시지 수신 정보를 생성할 수 있다.
    - [ ]  특정 채널의 메시지 수신 정보를 수정할 수 있다.
    - [ ]  특정 사용자의 메시지 수신 정보를 조회할 수 있다.
     */

    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<ReadStatus> createReadStatus(@Valid @RequestBody ReadStatusCreateDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(dto));
    }

    @RequestMapping(path="/{readstatusid}",method= RequestMethod.PATCH)
    public ResponseEntity<ReadStatus> updateReadStatus(@PathVariable UUID readstatusid,
                                                           @RequestBody ReadStatusUpdateDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.update(readstatusid,dto));
    }

    @RequestMapping(method= RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllReadStatusByUser(@RequestHeader UUID userId){
        return ResponseEntity.status(HttpStatus.OK).body(readStatusService.findAllByUserId(userId));
    }
}
