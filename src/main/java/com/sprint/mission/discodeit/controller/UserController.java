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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 등록
    @RequestMapping(value = "/api/user", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest request) {
        User user = userService.create(request, Optional.empty());
        return ResponseEntity.ok(user);
    }

    // 사용자 정보 수정
    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.PUT)
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequest request) {
        User user = userService.update(id, request, Optional.empty());
        return ResponseEntity.ok(user);
    }

    // 사용자 삭제
    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 사용자의 온라인 상태 업데이트
    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatus> updateStatus(@PathVariable UUID id, @RequestBody UserStatusUpdateRequest request) {
        UserStatus status = userStatusService.updateByUserId(id, request);
        return ResponseEntity.ok(status);
    }

    // 모든 사용자 조회(수정)
    @RequestMapping(method = RequestMethod.GET, value = "/api/user/findAll")
    public ResponseEntity<List<UserDto>> findAllUsers() {
        List<UserDto> userDto = userService.findAll();
        return ResponseEntity.ok(userDto);
    }

}