package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    //특정 채널의 메시지 수신 정보를 생성할 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(@ModelAttribute ReadStatusDto.Create request) {
        ReadStatusDto.Response response = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //특정 채널의 메시지 수신 정보를 수정할 수 있다.
    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<?> update(@ModelAttribute ReadStatusDto.Update request) {
        ReadStatusDto.Response response = readStatusService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //특정 사용자의 메시지 수신 정보를 조회할 수 있다.
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> findAllByUserId(@PathVariable UUID userId) {
        List<ReadStatusDto.Response> responses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
