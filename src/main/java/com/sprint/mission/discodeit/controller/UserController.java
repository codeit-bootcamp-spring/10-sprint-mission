package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // user 등록
    @PostMapping
    public UserResponse postUser(@RequestBody UserCreateRequest request){
        return userService.create(request);
    }

    // user 정보 수정
    @PostMapping("/update")
    public UserResponse putUser(@RequestBody UserUpdateRequest request){
        return userService.update(request);
    }

    // user 삭제
    @DeleteMapping("/{user-id}")
    public void deleteUser(@PathVariable("user-id") UUID userID){
        userService.deleteUser(userID);
    }

    // user 단건 조회
    @GetMapping("/{user-id}")
    public UserResponse getUser(@PathVariable("user-id") UUID userID){
        return userService.find(userID);
    }

    // user 다건 조회
    @GetMapping
    public List<UserResponse> getAllUsers(){
        return userService.findAll();
    }

    // user status 업데이트
    @PatchMapping("/{user-id}/status")
    public UserStatusResponse updateStatus(@PathVariable("user-id") UUID userID){
        return userStatusService.updateByUserID(userID);
    }

}
