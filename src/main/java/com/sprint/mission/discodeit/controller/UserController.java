package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;
  private final UserMapper userMapper;
  private final UserStatusMapper userStatusMapper;

  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> create(
      @RequestPart("userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    User user = userService.create(
        request.username(),
        request.email(),
        request.password(),
        profile
    );

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userMapper.toDto(user));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    List<User> users = userService.findAll();
    List<UserDto> dtos = users.stream()
        .map(userMapper::toDto)
        .toList();

    return ResponseEntity.ok(dtos);
  }

  @Override
  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> update(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    User user = userService.update(
        userId,
        request.newUsername(),
        request.newEmail(),
        request.newPassword(),
        profile
    );

    return ResponseEntity.ok(userMapper.toDto(user));
  }

  @Override
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatusDto> updateStatus(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request) {
    UserStatus status = userStatusService.updateByUserId(userId, request.newLastActiveAt());

    return ResponseEntity.ok(userStatusMapper.toDto(status));
  }

  @Override
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.deleteById(userId);
    return ResponseEntity.noContent().build();
  }
}
