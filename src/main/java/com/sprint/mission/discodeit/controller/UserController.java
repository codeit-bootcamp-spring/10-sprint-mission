package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  //사용자를 등록할 수 있다.
  @PostMapping
  @Operation(summary = "User 등록")
  public ResponseEntity<?> create(@RequestBody UserDto.Create request) {
    UserDto.Response response = userService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  //사용자 정보를 수정할 수 있다.
  @PatchMapping("/{userId}")
  @Operation(summary = "User 정보 수정")
  public ResponseEntity<?> update(
      @PathVariable UUID userId,
      @RequestBody UserDto.Update request
  ) {
    UserDto.Response response = userService.update(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  //사용자를 삭제할 수 있다.
  @DeleteMapping("/{userId}")
  @Operation(summary = "User 삭제")
  public ResponseEntity<?> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  //특정 사용자를 조회할 수 있다.
  @GetMapping("/{userId}")
  @Operation(summary = "특정 User 조회")
  public ResponseEntity<?> findById(@PathVariable UUID userId) {
    UserDto.Response response = userService.findById(userId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  //모든 사용자를 조회할 수 있다.
  @GetMapping
  @Operation(summary = "전체 User 목록 조회")
  public ResponseEntity<?> findAll() {
    List<UserDto.Response> responses = userService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }

  //사용자의 온라인 상태를 업데이트할 수 있다.
  @PatchMapping("/{userId}/userStatus")
  @Operation(summary = "User 온라인 상태 업데이트")
  public ResponseEntity<?> updateOnline(
      @PathVariable UUID userId,
      @RequestBody UserStatusDto.Update request) {
    UserStatusDto.Response response = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
