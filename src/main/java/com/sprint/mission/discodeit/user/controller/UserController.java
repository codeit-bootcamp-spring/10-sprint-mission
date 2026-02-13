package com.sprint.mission.discodeit.user.controller;


import com.sprint.mission.discodeit.user.dto.UserCreateInfo;
import com.sprint.mission.discodeit.user.dto.UserInfo;
import com.sprint.mission.discodeit.user.dto.UserInfoWithStatus;
import com.sprint.mission.discodeit.user.dto.UserUpdateInfo;
import com.sprint.mission.discodeit.user.service.UserService;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusInfo;
import com.sprint.mission.discodeit.userstatus.service.UserStatusService;
import lombok.RequiredArgsConstructor;
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

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserInfoWithStatus> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.findUser(userId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserInfoWithStatus>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<UserInfo> createUser(@RequestBody UserCreateInfo createInfo) {
        return ResponseEntity.ok(userService.createUser(createInfo));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserUpdateInfo updateInfo
    ) {
        userService.updateUser(userId, updateInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/status/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatusInfo> updateUserStatusByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(userStatusService.updateUserStatusByUserId(userId));
    }

    @RequestMapping(value = "/status/{statusId}", method = RequestMethod.GET)
    public ResponseEntity<UserStatusInfo> getUserStatus(@PathVariable UUID statusId) {
        return ResponseEntity.ok(userStatusService.findUserStatus(statusId));
    }
}
