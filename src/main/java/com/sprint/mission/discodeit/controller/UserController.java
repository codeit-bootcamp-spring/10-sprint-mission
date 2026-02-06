package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 유저 생성
    @PostMapping
    public ResponseEntity<UserResponseDto> postUser(@RequestBody UserCreateDto dto){
        UserResponseDto response = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 유저 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable UUID id){
        UserResponseDto response = userService.findUser(id);
        return ResponseEntity.ok(response);
    }

    // 유저 전체 조회

    // 유저 업데이트
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID id,
                                                      @RequestBody UserUpdateDto dto){
        UserResponseDto response = userService.update(id,dto);
        return ResponseEntity.ok(response);
    }

    // 유저 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        userService.delete(id);
        return ResponseEntity.noContent().build(); // noContent는 객체를 만든다는 뜻, build를 붙혀서 객체 만듬

    }

}
