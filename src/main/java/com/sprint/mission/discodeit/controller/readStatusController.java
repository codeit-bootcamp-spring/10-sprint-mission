package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@Tag(name = "ReadStatus", description = "읽음 상태 관련 API")
@RequiredArgsConstructor
public class readStatusController {
    private final ReadStatusService readStatusService;
    // 메시지 수신 상태 생성
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto> postReadStatus(@Valid @RequestBody ReadStatusCreateRequest dto){
        ReadStatusDto response = readStatusService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // 특정 유저의 모든 메시지 수신 상태 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto>> getAllReadStatus(@RequestParam("userId") UUID userId){
        List<ReadStatusDto> response = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(response);
    }

    // 메시지 수신 상태 수정
    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusDto> updateReadStatus(@PathVariable("readStatusId") UUID id,
                                                          @Valid @RequestBody ReadStatusUpdateRequest request){
        ReadStatusDto response = readStatusService.update(id,request.getNewLastReadAt());
        return ResponseEntity.ok(response);
    }
}
