package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusServiceDTO.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.command.UserStatusUpdateCommand;
import com.sprint.mission.discodeit.dto.userstatus.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserResponse;
import com.sprint.mission.discodeit.dto.user.command.UserCreateCommand;
import com.sprint.mission.discodeit.dto.user.command.UserUpdateCommand;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponse> find(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.find(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> create(
            @RequestPart @Valid UserCreateRequest userCreateRequest,
            @RequestPart @Nullable MultipartFile profile) {
        UserCreateCommand command = new UserCreateCommand(userCreateRequest, profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(command));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @RequestPart @Valid UserUpdateRequest userUpdateRequest,
            @RequestPart @Nullable MultipartFile profile) {
        UserUpdateCommand command = new UserUpdateCommand(id, userUpdateRequest, profile);
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(command));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping(value = "/{id}/userStatus")
    public ResponseEntity<UserStatusResponse> updateActiveStatus(@PathVariable UUID id,
                                                                 @RequestBody @Valid UserStatusUpdateRequest request) {
        UserStatusUpdateCommand command = new UserStatusUpdateCommand(id, request.datetime());
        return ResponseEntity.status(HttpStatus.OK).body(userStatusService.update(command));
    }
}
