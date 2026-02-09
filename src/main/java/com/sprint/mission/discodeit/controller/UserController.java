package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateByUserIdDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 등록
    @RequestMapping(method = RequestMethod.POST)
    public UserResponseDto join(@RequestBody UserCreateDto dto) {
        return userService.create(dto);
    }

    // 사용자 정보 수정
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public UserResponseDto update(@PathVariable UUID id,
                                  @RequestBody UserUpdateDto dto) {
        return userService.update(id, dto);
    }

    // 사용자 삭제
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID id){
        userService.delete(id);
    }

    // 모든 사용자 조회
    @RequestMapping(value = "/api/user/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponseDto>> findAll(){
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    // 사용자 온라인 상태 업데이트
    @RequestMapping(value = "/{id}/status", method = RequestMethod.PATCH)
    public UserResponseDto updateStatus(@PathVariable UUID id,
                                        @RequestBody UserStatusUpdateByUserIdDto dto) {
        return userStatusService.updateByUserId(id, dto);
    }
}
