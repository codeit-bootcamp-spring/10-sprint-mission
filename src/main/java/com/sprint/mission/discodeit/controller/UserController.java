package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;


//    [ ] 사용자를 등록할 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto> postUser(@RequestBody UserCreateRequest request) {
        User created = userService.create(request, Optional.empty());
        UserDto body = userService.find(created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }


    // [ ] 사용자 단건 조회
    @RequestMapping(value = "/{user-id}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> getUser(@PathVariable("user-id") UUID userId) {
        return ResponseEntity.ok(userService.find(userId));
    }


//    [ ] 모든 사용자를 조회할 수 있다.
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }


//     [ ] 사용자 정보를 수정할 수 있다.
    @RequestMapping(value = "/{user-id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("user-id") UUID userId,
            @RequestBody UserUpdateRequest request
    ) {
        userService.update(userId, request, Optional.empty());
        return ResponseEntity.ok(userService.find(userId));
    }


//     [ ] 사용자를 삭제할 수 있다.
    @RequestMapping(value = "/{user-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }


//    [ ] 사용자의 온라인 상태를 업데이트할 수 있다.
    @RequestMapping(value = "/{user-id}/status", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatus> updateOnlineStatus(
            @PathVariable("user-id") UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatus updated = userStatusService.updateByUserId(userId, request);
        return ResponseEntity.ok(updated);
    }
}
