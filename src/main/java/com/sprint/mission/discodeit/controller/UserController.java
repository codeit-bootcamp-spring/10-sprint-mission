package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatus;

    @RequestMapping(method = RequestMethod.POST, value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto.Response> createUser(
            @RequestPart("request") @Valid UserDto.CreateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile file) {
        UserDto.Response response = userService.create(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{user-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto.Response> updateUser(
            @PathVariable("user-id") UUID userId,
            @RequestPart(value = "request", required = false) @Valid UserDto.UpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile file) {
        UserDto.Response response = userService.update(userId, request, file);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{user-id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user-id}")
    public ResponseEntity<UserDto.Response> findUser(@PathVariable("user-id") UUID userId) {
        UserDto.Response response = userService.find(userId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findAll")
    public ResponseEntity<List<UserDto.Response>> findAllUser() {
        List<UserDto.Response> response = userService.findAll();
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{user-id}/status")
    public ResponseEntity<UserStatusDto.Response> updateUserStatus(
            @PathVariable("user-id") UUID userId,
            @RequestBody UserStatusDto.UpdateRequest request) {
        UserStatusDto.Response response = userStatus.updateByUserId(userId, request);
        return ResponseEntity.ok(response);
    }
}
