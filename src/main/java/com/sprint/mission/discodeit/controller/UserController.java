package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
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
    @RequestMapping(method = RequestMethod.GET, path = "")
    public ResponseEntity<List<UserDto.UserResponse>> findAllUsers() {
        List<UserDto.UserResponse> userDatas = userService.findAll();
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

    // 채널 참가
    @RequestMapping(method = RequestMethod.POST, path = "/{userId}/channels/{channelId}")
    public ResponseEntity<Void> joinChannel(
            @PathVariable UUID userId,
            @PathVariable UUID channelId
    ) {
        userService.joinChannel(userId, channelId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    // 채널 탈퇴
    @RequestMapping(method = RequestMethod.DELETE, path = "/{userId}/channels/{channelId}")
    public ResponseEntity<Void> leaveChannel(
            @PathVariable UUID userId,
            @PathVariable UUID channelId
    ) {
        userService.leaveChannel(userId, channelId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

}
