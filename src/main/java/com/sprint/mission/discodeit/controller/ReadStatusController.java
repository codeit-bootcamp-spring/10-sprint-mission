package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/readstatuses")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // 특정 채널 메시지 수신 정보 생성
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ReadStatusResponseDTO> create(@RequestBody ReadStatusCreateRequestDTO readStatusCreateRequestDTO) {
        ReadStatusResponseDTO response = readStatusService.create(readStatusCreateRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 특정 채널 메시지 수신 정보를 수정
    @RequestMapping(value = "/{readstatus-id}", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity<ReadStatusResponseDTO> update(@PathVariable("readstatus-id") UUID readStatusId,
                                                        @RequestBody ReadStatusUpdateRequestDTO readStatusUpdateRequestDTO) {
        ReadStatusResponseDTO response = readStatusService.update(readStatusId, readStatusUpdateRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 특정 사용자의 메시지 수신 정보를 조회
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ReadStatusResponseDTO>> findAllByUserId(@RequestParam UUID userId) {
        List<ReadStatusResponseDTO> response = readStatusService.findAllByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
