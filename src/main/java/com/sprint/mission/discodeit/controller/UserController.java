package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 회원가입
    @RequestMapping(method = RequestMethod.POST, path = "/signup")
    public ResponseEntity<UserDto.UserResponse> createUser(
            @RequestBody UserDto.UserRequest request,
            @RequestParam(required = false) String profileImagePath
    ) {
        UserDto.UserResponse userData = userService.create(request, profileImagePath);
        return new ResponseEntity<>(userData, HttpStatus.CREATED);
    }

    // 회원 조회
    @RequestMapping(method = RequestMethod.GET, path = "/{userId}")
    public ResponseEntity<UserDto.UserResponse> findUserById(
            @PathVariable UUID userId
    ) {
        UserDto.UserResponse userData = userService.findById(userId);
        return new ResponseEntity<>(userData, HttpStatus.OK);
    }

    // 전체 회원 조회
    @RequestMapping(method = RequestMethod.GET, path = "/api/user/findAll")
    public ResponseEntity<List<UserDto.FindAllUserResponse>> findAllUsers() {
        List<UserDto.FindAllUserResponse> userDatas = userService.findAll();
        return new ResponseEntity<>(userDatas, HttpStatus.OK);
    }

    // 유저 정보 수정
    @RequestMapping(method = RequestMethod.PUT, path = "/{userId}")
    public ResponseEntity<UserDto.UserResponse> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserDto.UserRequest request,
            @RequestParam(required = false) String profileImagePath
    ) {
        UserDto.UserResponse updated = userService.update(userId, request, profileImagePath);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // 유저 삭제
    @RequestMapping(method = RequestMethod.DELETE, path = "/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId
    ) {
        userService.delete(userId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    // 유저 온라인 상태 갱신
    @RequestMapping(method = RequestMethod.PATCH, path = "/{userId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusDto.UserStatusUpdateRequest request
    ) {
        userStatusService.updateByUserId(userId, request.status());
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

}
