package com.sprint.mission.discodeit.user.controller;

import com.sprint.mission.discodeit.common.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.dto.response.UserResponse;
import com.sprint.mission.discodeit.user.service.UserService;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusDto;
import com.sprint.mission.discodeit.userstatus.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.userstatus.service.UserStatusService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
      @RequestPart(required = false) MultipartFile profile) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.create(userCreateRequest, FileUploadRequest.from(profile)));
  }

  @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> update(
      @PathVariable UUID id,
      @RequestPart @Valid UserUpdateRequest userUpdateRequest,
      @RequestPart(required = false) MultipartFile profile) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(userService.update(id, userUpdateRequest, FileUploadRequest.from(profile)));
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    userService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping(value = "/{userId}/userStatus")
  public ResponseEntity<UserStatusDto> updateUserStatus(
      @PathVariable UUID userId,
      @RequestBody @Valid UserStatusUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(userStatusService.update(userId, request));
  }

}
