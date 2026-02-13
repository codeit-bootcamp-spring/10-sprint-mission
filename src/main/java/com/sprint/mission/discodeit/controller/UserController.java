package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    // user 등록
    @RequestMapping(method = RequestMethod.POST)
    public UserResponse postUser(@RequestBody UserCreateRequest request){
        return userService.create(request, Optional.ofNullable(request.profileImage()));
    }

    // user 정보 수정
    @RequestMapping(value="/{user-id}",method = RequestMethod.PATCH)
    public UserResponse updateUser(@PathVariable("user-id") UUID userID,
                                   @RequestBody UserUpdateRequest request){
        return userService.update(userID, request, Optional.ofNullable(request.profileImage()));
    }

    // user 삭제
    @RequestMapping(method = RequestMethod.DELETE, value = "/{user-id}")
    public void deleteUser(@PathVariable("user-id") UUID userID){
        userService.deleteUser(userID);
    }

    // user 단건 조회
    @RequestMapping(method = RequestMethod.GET, value = "/{user-id}")
    public UserResponse getUser(@PathVariable("user-id") UUID userID){
        return userService.find(userID);
    }

    // user 다건 조회
    @RequestMapping(value="/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // user status 업데이트
    @RequestMapping(method = RequestMethod.PATCH, value = "/{user-id}/status")
    public UserStatusResponse updateStatus(@PathVariable("user-id") UUID userID){
        return userStatusService.updateByUserID(userID);
    }

}
