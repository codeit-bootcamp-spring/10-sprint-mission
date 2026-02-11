package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/readstatus")
public class ReadStatusController {
    private final ReadStatusService readStatusService;


//    [ ] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> postReadStatus(@RequestBody ReadStatusCreateRequest request) {
        ReadStatus created = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

//    [ ] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
    @RequestMapping(value = "/channels", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatus> updateReadStatus(@RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatus updated = readStatusService.update(request);
        return ResponseEntity.ok(updated);
    }


//     [ ] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
    @RequestMapping(value = "/users/{user-id}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> getReadStatusesByUser(@PathVariable("user-id") UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}
