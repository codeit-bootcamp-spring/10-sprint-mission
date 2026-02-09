package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    // 사용자 등록
    @RequestMapping(method = RequestMethod.POST)
    public User createUser(@RequestBody UserCreateRequest request) {
        return userService.create(request, Optional.empty());
    }

    // 사용자 정보 수정
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public User updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequest request) {
        return userService.update(id, request, Optional.empty());
    }

    // 사용자 삭제
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable UUID id) {
        userService.delete(id);
    }

    // 모든 사용자 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }

    // 사용자의 온라인 상태 업데이트
    @RequestMapping(value = "/{id}/status", method = RequestMethod.PATCH)
    public UserStatus updateStatus(@PathVariable UUID id, @RequestBody UserStatusUpdateRequest request) {
        return userStatusService.updateByUserId(id, request);
    }

}