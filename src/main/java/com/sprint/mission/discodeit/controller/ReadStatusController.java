package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/read-status")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponseDTO> create (@RequestBody ReadStatusCreateRequestDTO readStatusCreateRequestDTO) {
        ReadStatusResponseDTO newReadStatus = readStatusService.create(readStatusCreateRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newReadStatus);
    }

    // 특정 채널의 메시지 수신 정보 수정
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ReadStatusResponseDTO> update(@PathVariable UUID id,
                                                        @RequestBody ReadStatusUpdateRequestDTO readStatusUpdateRequestDTO) {
        readStatusUpdateRequestDTO.setId(id);
        ReadStatusResponseDTO updateReadStatus = readStatusService.update(readStatusUpdateRequestDTO);
        return ResponseEntity.ok(updateReadStatus);
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<ReadStatusResponseDTO> findByUserId(@PathVariable UUID userId) {
        ReadStatusResponseDTO readStatus = readStatusService.findByUserId(userId);

        return ResponseEntity.ok(readStatus);
    }
}
