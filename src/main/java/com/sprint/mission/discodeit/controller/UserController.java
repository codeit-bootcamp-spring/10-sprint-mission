package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest request) {
        UserResponse response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> findById(@PathVariable UUID userId) {
        UserResponse response = userService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> response = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<UserResponse> update(@PathVariable UUID userId,
                                               @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.update(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID userId) {
        userService.delete(userId);
    }
}
