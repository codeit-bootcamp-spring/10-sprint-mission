package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 메시지 수신 정보 생성
    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<ReadStatusDto.ReadStatusResponse> create(
            @RequestBody ReadStatusDto.ReadStatusRequest request
    ) {
        ReadStatusDto.ReadStatusResponse created =
                readStatusService.create(request.userId(), request.channelId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 메시지 수신 정보 수정
    @RequestMapping(method = RequestMethod.PUT, path = "/{readStatusId}")
    public ResponseEntity<ReadStatusDto.ReadStatusResponse> update(
            @PathVariable UUID readStatusId,
            @RequestBody Map<String, String> body
    ) {
        String lastReadTimeStr = body.get("lastReadTime");
        if (lastReadTimeStr == null) {
            throw new IllegalArgumentException("lastReadTime 값이 필요합니다.");
        }

        Instant lastReadTime = Instant.parse(lastReadTimeStr);

        ReadStatusDto.ReadStatusResponse updated =
                readStatusService.update(readStatusId, lastReadTime);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(method = RequestMethod.GET, path = "")
    public ResponseEntity<List<ReadStatusDto.ReadStatusResponse>> findAllByUserId(
            @RequestParam UUID userId
    ) {
        List<ReadStatusDto.ReadStatusResponse> readStatuses =
                readStatusService.findAllByUserId(userId);

        return new ResponseEntity<>(readStatuses, HttpStatus.OK);
    }
}
