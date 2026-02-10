package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    // 유저 생성
    @RequestMapping(method = RequestMethod.POST)
    public UserResponse postUser(@RequestBody UserCreateRequest dto) {
        return userService.create(dto);
    }

    //유저 수정
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    public UserResponse putUser(
            @PathVariable UUID userId, @RequestBody UserUpdateRequest dto) {
        if(dto == null || dto.userId() == null) {
            throw new IllegalArgumentException("userId null이 될 수 없습니다.");
        }
        if(!userId.equals(dto.userId())) {
            throw new IllegalArgumentException("path userId와 body userId가 일치해야 합니다.");
        }
        return userService.update(dto);
    }
    //유저 삭제
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
    }

    // 모든 유저 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> getUser() {
        return userService.findAllDto();
    }

    // 유저 상태 업데이트
    //PUT /users/{userId}/online?online=true
    //PUT /users/{userId}/online?online=false
    @RequestMapping(value = "/{userId}/online", method = RequestMethod.PUT)
    public UserStatusResponse putUserOnline(
            @PathVariable UUID userId,
            @RequestParam boolean online
    ){
        return userStatusService.updateOnline(userId, online);
    }

}
