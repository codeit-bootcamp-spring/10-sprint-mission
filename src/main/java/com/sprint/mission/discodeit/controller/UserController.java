package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequest request){
        return ResponseEntity.ok(userService.update(id, request));
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value= "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> findAll(){
        List<UserResponse> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @RequestMapping(value = "/{id}/status", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatusResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody UserStatusUpdateRequest request){
        return ResponseEntity.ok(userStatusService.updateByUserId(id, request));
    }
}
