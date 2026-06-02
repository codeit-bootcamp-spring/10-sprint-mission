package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserStatusApi;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/userStatuses")
@RequiredArgsConstructor
public class UserStatusController implements UserStatusApi {

    private final UserStatusService userStatusService;

    @Override
    @RequestMapping (method = RequestMethod.GET, value = "/{userId}")
    public ResponseEntity<UserStatusDto.Response> findByUserId(@PathVariable UUID userId) {
        UserStatusDto.Response response = userStatusService.findByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
