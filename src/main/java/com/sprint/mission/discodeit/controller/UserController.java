package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;
  private final UserMapper userMapper;

  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> create(
      @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    log.info("Received POST /api/users API request - username: {}",
        request.username()); // 유저 생성 요청 로그

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
    log.info("Received GET /api/users API request"); // 유저 조회 요청 로그

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
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    log.info("Received PATCH /api/users/{} API request", userId); // 유저 정보 수정 요청 로그

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
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    log.info("Received DELETE /api/users/{} API request", userId); // 유저 삭제 요청 로그

    userService.deleteById(userId);
    return ResponseEntity.noContent().build();
  }
}
