package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ResourceBundle;
import java.util.UUID;

@RestController
@RequestMapping("/read-status")
@RequiredArgsConstructor
public class readStatusController {
    private final ReadStatusService readStatusService;
    // 메시지 수신 상태 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponseDto> getReadStatus(@RequestBody ReadStatusCreateDto dto){
        ReadStatusResponseDto response = readStatusService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // 메시지 수신 상태 조회
    @RequestMapping(value = "/channel/{channel-id}/user/{user-id}", method = RequestMethod.GET)
    public ResponseEntity<ReadStatusResponseDto> getReadStatus(@PathVariable("channel-id") UUID channelId,
                                                               @PathVariable("user-id") UUID userId){
        ReadStatusResponseDto response = readStatusService.findReadStatus(channelId, userId);
        return ResponseEntity.ok(response);
    }

    // 메시지 수신 상태 수정
    @RequestMapping(value = "/channel/{channel-id}/user/{user-id}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponseDto> updateReadStatus(@PathVariable("channel-id") UUID channelId,
                                                               @PathVariable("user-id") UUID userId){
        ReadStatusResponseDto response = readStatusService.update(channelId, userId);
        return ResponseEntity.ok(response);
    }
}
