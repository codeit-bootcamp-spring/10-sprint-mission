package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusRequestUpdateDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.user.UserRequestCreateDto;
import com.sprint.mission.discodeit.dto.user.UserRequestUpdateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @Autowired
    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }


    // 1. 사용자 등록
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<UserResponseDto> create(@RequestBody UserRequestCreateDto requestCreateDto) {
        UserResponseDto urDto = userService.create(requestCreateDto);
        return ResponseEntity.ok(urDto);
    }

    // 2. 사용자 정보 수정
    @RequestMapping(value = "/update",  method = RequestMethod.PATCH)
    public ResponseEntity<UserResponseDto> update(@RequestBody UserRequestUpdateDto requestUpdateDto) {
        UserResponseDto urDto = userService.update(requestUpdateDto);
        return ResponseEntity.ok(urDto);
    }

    // 3. 사용자 삭제
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam UUID id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    // 4. 모든 사용자 조회
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponseDto>> findAll() {
       userService.findAll();
       return ResponseEntity.ok().body(userService.findAll());
    }

    // 5. 사용자의 온라인 상태를 업데이트
    @RequestMapping(value = "/update/online",  method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateOnline(@RequestBody UUID userid) {
        userStatusService.updateByUserId(userid);
        return ResponseEntity.ok().build();
    }
}
