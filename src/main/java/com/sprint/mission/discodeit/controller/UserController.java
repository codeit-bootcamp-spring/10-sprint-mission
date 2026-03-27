package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentProcessingException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;
  private final ObjectMapper objectMapper;

  // Mapper 주입
  private final UserMapper userMapper;
  private final UserStatusMapper userStatusMapper;

  // POST /api/users -> 201
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> create(
          @RequestPart("userCreateRequest") @Valid UserCreateRequest userCreateRequest,
          @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws Exception {

    log.info("HTTP 요청 - 유저 생성");

    // resolveProfile -> 없으면 empty 있으면 변환 시도
    Optional<BinaryContentCreateRequest> profileRequest =
            Optional.ofNullable(profile).flatMap(this::resolveProfileRequest);

    User createdUser = userService.create(userCreateRequest, profileRequest);

    log.info("HTTP 응답 - 유저 생성 완료 userId={}", createdUser.getId());

    // Entity -> Dto mapper
    UserDto dto = userMapper.toDto(createdUser);

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }


  // PATCH /api/users/{userId}
  @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> update(
          @PathVariable UUID userId,
          @RequestPart("userUpdateRequest") @Valid UserUpdateRequest userUpdateRequest, //String userUpdateRequestJson,
          @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws Exception {

    log.info("HTTP 요청 - 유저 수정 userId={}", userId);

    Optional<BinaryContentCreateRequest> profileRequest =
            Optional.ofNullable(profile).flatMap(this::resolveProfileRequest);

    User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);

    log.info("HTTP 응답 = 유저 수정 완료 userId={}", userId);

    UserDto dto = userMapper.toDto(updatedUser);
    return ResponseEntity.ok(dto);
  }

  // DELETE /api/users/{userId} -> 204
  @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {

    log.info("HTTP 요청 - 유저 삭제 userId={}", userId);

    userService.delete(userId);

    log.info("HTTP 응답 - 유저 삭제 완료 userId={}", userId);

    return ResponseEntity.noContent().build();
  }

  // GET /api/users
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {

    log.debug("HTTP 요청 - 유저 전체 조회");

    List<User> users = userService.findAll();
    List<UserDto> dtos = users.stream()
            .map(userMapper::toDto)
            .toList();
    return ResponseEntity.ok(dtos);
  }

  // PATCH /api/users/{userId}/userStatus
  @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
  public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
          @PathVariable UUID userId,
          @Valid @RequestBody UserStatusUpdateRequest request
  ) {

    log.info("HTTP 요청 - 유저 상태 수정 userId={}", userId);

    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);

    // userStatus도 그대로 반환 대신 dto로 변환.
    UserStatusDto dto = userStatusMapper.toDto(updatedUserStatus);

    log.info("HTTP 응답 - 유저 상태 수정 완료 userId={}", userId);

    return ResponseEntity.ok(dto);
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile == null || profileFile.isEmpty()) return Optional.empty();
    try {
      return Optional.of(new BinaryContentCreateRequest(
              profileFile.getOriginalFilename(),
              profileFile.getContentType(),
              profileFile.getBytes()
      ));
    } catch (IOException e) {
      throw new BinaryContentProcessingException("JSON parsing failed");
    }
  }
}