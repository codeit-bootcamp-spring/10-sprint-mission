package com.sprint.mission.discodeit.user.controller;

import com.sprint.mission.discodeit.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.UserResponse;
import com.sprint.mission.discodeit.user.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.user.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.user.service.UserService;
import com.sprint.mission.discodeit.user.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.GET)
    public List<UserResponse> getAllUsers() {
        return userService.findAll();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public UserResponse getUserById(@PathVariable UUID userId) {
        return userService.find(userId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public UserResponse createUser(@RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public UserResponse updateUser(@RequestBody UserUpdateRequest request) {
        return userService.update(request.id(),request);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    public void updateOnlineStatus(@PathVariable UUID userId) {
        userStatusService.updateByUserId(userId);
    }
}
