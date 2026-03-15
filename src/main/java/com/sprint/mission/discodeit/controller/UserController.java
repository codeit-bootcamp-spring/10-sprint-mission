package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.controller.support.BinaryContentRequestResolver;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;
  private final BinaryContentRequestResolver binaryContentRequestResolver;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<UserDto> create(
          @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
          @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userService.create(
                    userCreateRequest,
                    binaryContentRequestResolver.resolveOptional(profile)
            ));
  }

  @PatchMapping(path = "{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<UserDto> update(
          @PathVariable("userId") UUID userId,
          @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
          @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.update(
                    userId,
                    userUpdateRequest,
                    binaryContentRequestResolver.resolveOptional(profile)
            ));
  }

  @DeleteMapping("{userId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
    userService.delete(userId);
    return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
  }

  @GetMapping
  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.findAll());
  }

  @PatchMapping("{userId}/userStatus")
  @Override
  public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
          @PathVariable("userId") UUID userId,
          @RequestBody UserStatusUpdateRequest request
  ) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(userStatusService.updateByUserId(userId, request));
  }
}