package com.sprint.mission.discodeit.user.presentation.controller;

import com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.response.UserResponse;
import com.sprint.mission.discodeit.user.application.service.UserService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(value = "/{id}")
  public ResponseEntity<UserResponse> find(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.find(id));
  }

  @GetMapping
  public ResponseEntity<List<UserResponse>> findAll() {
    return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> create(
      @RequestBody @Valid UserCreateRequest userCreateRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.create(userCreateRequest));
  }

  @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> update(
      @PathVariable UUID id,
      @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(userService.update(id, userUpdateRequest));
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    userService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PutMapping("/role")
  public ResponseEntity<UserResponse> updateRole(@RequestBody RoleUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.updateRole(request));
  }
}
