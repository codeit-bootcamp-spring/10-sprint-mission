package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class readStatusController {
    private final ReadStatusService readStatusService;
    // 메시지 수신 상태 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponseDto> getReadStatus(@RequestBody ReadStatusCreateDto dto){
        ReadStatusResponseDto response = readStatusService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // 특정 유저의 모든 메시지 수신 상태 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponseDto>> getAllReadStatus(@RequestParam UUID userId,
                                                                        @RequestParam UUID channelId){
        List<ReadStatusResponseDto> response = readStatusService.findAllByUserIdChannelId(userId, channelId);
        return ResponseEntity.ok(response);
    }

    // 메시지 수신 상태 수정
    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponseDto> updateReadStatus(@PathVariable("readStatusId") UUID id,
                                                                  @RequestParam UUID channelId,
                                                                  @RequestParam UUID userId){
        ReadStatusResponseDto response = readStatusService.update(id);
        return ResponseEntity.ok(response);
    }
}
