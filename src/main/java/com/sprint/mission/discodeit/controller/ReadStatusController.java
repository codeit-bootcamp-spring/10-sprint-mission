package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusRequestCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusRequestUpdateDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readstatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    // 1. 특정 채널의 메시지 수신 정보 생성
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponseDto> create(@RequestBody ReadStatusRequestCreateDto readStatusCreateDto) {
        ReadStatusResponseDto rsDto = readStatusService.create(readStatusCreateDto);
        return ResponseEntity.ok(rsDto);
    }

    // 2. 특정 채널의 메시지 수신 정보 수정
    @RequestMapping(value = "/update", method = RequestMethod.PATCH)
    public ResponseEntity<Void> update(@RequestBody ReadStatusRequestUpdateDto readStatusUpdateDto) {
        readStatusService.update(readStatusUpdateDto);
        return ResponseEntity.ok().build();
    }

    // 3. 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(value = "/findAllByUserId", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponseDto>> findAllByUserId(@RequestParam UUID id) {
       List<ReadStatusResponseDto> rsDto = readStatusService.findAllByUserId(id);
        return ResponseEntity.ok(rsDto);
    }


}
