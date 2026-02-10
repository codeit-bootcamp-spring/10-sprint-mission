package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping("/api/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @PatchMapping("/api/users/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequest request){
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/user/findAll")
    public ResponseEntity<List<UserDto>> findAll(){
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/api/users/{id}/status")
    public ResponseEntity<UserStatusResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody UserStatusUpdateRequest request){
        return ResponseEntity.ok(userStatusService.updateByUserId(id, request));
    }
}
