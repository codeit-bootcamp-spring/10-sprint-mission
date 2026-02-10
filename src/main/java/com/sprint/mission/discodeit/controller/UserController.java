package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 유저 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponseDto> postUser(@ModelAttribute UserCreateDto dto){
        UserResponseDto response = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 유저 단건 조회
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserResponseDto> getUser(@PathVariable UUID id){
        UserResponseDto response = userService.findUser(id);
        return ResponseEntity.ok(response);
    }

    // 유저 전체 조회
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponseDto>> getAllUser(){
        List<UserResponseDto> userList = userService.findAllUsers();
        return ResponseEntity.ok(userList);
    }

    // 유저 업데이트
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID id,
                                                      @RequestBody UserUpdateDto dto){
        UserResponseDto response = userService.update(id,dto);
        return ResponseEntity.ok(response);
    }

    // 유저 온라인상태 업데이트
    @RequestMapping(value = "/{id}/online-status" , method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateOnline(@PathVariable UUID id){
        userService.updateOnlineStatus(id);
        return ResponseEntity.ok().build();
    }

    // 유저 삭제
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        userService.delete(id);
        return ResponseEntity.noContent().build(); // noContent는 객체를 만든다는 뜻, build를 붙혀서 객체 만듬

    }

}
