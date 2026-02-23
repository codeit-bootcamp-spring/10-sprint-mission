package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    // 사용자 등록
    @PostMapping
    public ResponseEntity<UserDto> postUser(@RequestBody UserCreateRequest request) {
        System.out.println(request);
        User user = userService.create(request, Optional.empty());
        UserDto userDto = userService.find(user.getId());
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    // 사용자 정보 수정
//    @RequestMapping(value = "/user/{userId}", method = RequestMethod.PATCH)
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> putUser(@PathVariable UUID userId,
                                           @RequestBody UserUpdateRequest request) {
        User user = userService.update(userId, request, Optional.empty());
        System.out.println(user.getUsername());
        System.out.println(request);
        UserDto userDto = userService.find(userId);
        System.out.println(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }


//    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
//    public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
//        UserDto userDto = userService.find(userId);
//        return new ResponseEntity<>(userDto, HttpStatus.OK);
//    }

    // 사용자 목록 조회
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> userDtos = userService.findAll();
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    // 모든 사용자 조회
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> userDtos = userService.findAll();
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    // 사용자 삭제
//    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        System.out.println("유저 삭제 시작");
        userService.delete(userId);
        System.out.println("유저 삭제 끝");
        return ResponseEntity.ok().build();
    }

    // 사용자 온라인 변경
//    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserDto> patchUserOnline(@PathVariable UUID userId,
                                                   @RequestBody UserStatusUpdateRequest userStatusUpdateRequest) {
        userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        UserDto userDto = userService.find(userId);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
