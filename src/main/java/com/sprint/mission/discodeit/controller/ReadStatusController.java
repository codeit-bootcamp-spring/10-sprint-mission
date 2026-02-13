package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.input.ReadStatusCreateInput;
import com.sprint.mission.discodeit.dto.readstatus.input.ReadStatusUpdateInput;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    /**
     * 특정 채널 메시지 수신 정보 생성
     * 채널 생성/참여 시 자동으로 생성되게 이미 설정되어 있음
     */
    @RequestMapping(value = "/read-status/{channelId}", method = RequestMethod.POST)
    public ResponseEntity createReadStatus(@PathVariable UUID channelId,
                                           @RequestBody @Valid ReadStatusCreateRequest request) {
        ReadStatusCreateInput input = new ReadStatusCreateInput(request.userId(), channelId);
        ReadStatus readStatus = readStatusService.createReadStatus(input);
        ReadStatusResponse result = createReadStatusResponse(readStatus);

        return ResponseEntity.status(201).body(result);
    }

    /**
     * 특정 채널의 메시지 수신 정보 수정
     */
    @RequestMapping(value = "/read-status/{readStatusId}", method = RequestMethod.PATCH)
    public ResponseEntity updateReadStatus(@PathVariable UUID readStatusId) {
        ReadStatusUpdateInput input = new ReadStatusUpdateInput(readStatusId, Instant.now());
        ReadStatus readStatus = readStatusService.updateReadStatus(input);
        ReadStatusResponse result = createReadStatusResponse(readStatus);

        return ResponseEntity.status(200).body(result);
    }

    /**
     * 특정 사용자의 메시지 수신 정보 조회
     */
    @RequestMapping(value = "/user/{userId}/read-status", method = RequestMethod.GET)
    public ResponseEntity findAllReadStatusByUserId(@PathVariable UUID userId) {
        List<ReadStatusResponse> result = readStatusService.findAllByUserId(userId);

        return ResponseEntity.status(200).body(result);
    }

    private ReadStatusResponse createReadStatusResponse(ReadStatus readStatus) {
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getLastReadTime()
        );
    }

}
