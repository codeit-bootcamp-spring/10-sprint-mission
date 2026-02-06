package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    /*
     **사용자 관리**
        - [ ]  사용자를 등록할 수 있다.
        - [ ]  사용자 정보를 수정할 수 있다.
        - [ ]  사용자를 삭제할 수 있다.
        - [ ]  모든 사용자를 조회할 수 있다.
        - [ ]  사용자의 온라인 상태를 업데이트할 수 있다.
     */
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<UserResponseDto> updateUser(@RequestHeader UUID userId, @Valid @RequestBody UserUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.update(userId, dto));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@RequestHeader UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponseDto>> findUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @RequestMapping(path="/status",method = RequestMethod.PATCH)
    public ResponseEntity<UserResponseDto> updateUserStatus(@RequestHeader UUID userId, @Valid @RequestBody UserStatusUpdateDto dto) {
        userStatusService.updateByUserId(userId,dto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.find(userId));
    }

    //추가
   //특정 사용자 조회 - 누구나 조회가능!
    @RequestMapping(path ="/{userid}" , method= RequestMethod.GET)
    public ResponseEntity<UserResponseDto> findUser(@PathVariable UUID userid) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.find(userid));
    }
}
