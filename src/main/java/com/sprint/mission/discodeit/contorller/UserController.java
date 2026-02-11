package com.sprint.mission.discodeit.contorller;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UUID> createUser(
            @ModelAttribute CreateUserRequest request
    ) {
        UUID userId = userService.createUser(request);

        return ResponseEntity.ok(userId);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> findUserByUserId(
            @PathVariable UUID userId
    ) {
        UserResponse response = userService.findUserByUserID(userId);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> findAllUsers() {

        List<UserResponse> responses = userService.findAllUsers();

        return ResponseEntity.ok(responses);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @RequestBody UpdateUserRequest request
    ) {
        UserResponse response = userService.updateUser(userId, request);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId
    ) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PUT)
    public ResponseEntity<UserStatusResponse> updateUserStatus(
            @PathVariable UUID userId
    ) {
        UserStatusResponse response =
                userStatusService.updateUserStatusByUserId(userId);

        return ResponseEntity.ok(response);
    }
}
