package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    //사용자를 등록할 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(@ModelAttribute UserDto.Create request) {
        UserDto.Response response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //사용자 정보를 수정할 수 있다.
    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> update(
            @PathVariable UUID userId,
            @ModelAttribute UserDto.Update request
    ) {
        UserDto.Response response = userService.update(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //사용자를 삭제할 수 있다.
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    //특정 사용자를 조회할 수 있다.
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> findById(@PathVariable UUID userId) {
        UserDto.Response response = userService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //모든 사용자를 조회할 수 있다.
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<?> findAll() {
        List<UserDto.Response> responses = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    //사용자의 온라인 상태를 업데이트할 수 있다.
    @RequestMapping(value = "/{userId}/online", method = RequestMethod.POST)
    public ResponseEntity<?> updateOnline(@PathVariable UUID userId) {
        UserStatusDto.Response response = userStatusService.updateByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
