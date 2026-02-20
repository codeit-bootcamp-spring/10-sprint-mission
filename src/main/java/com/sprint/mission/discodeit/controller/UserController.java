package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "User 등록")
  public ResponseEntity<?> create(
      @RequestPart("userCreateRequest") UserDto.Create request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    UserDto.Response response = userService.create(request, profile);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "User 정보 수정")
  public ResponseEntity<?> update(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserDto.Update request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    UserDto.Response response = userService.update(userId, request, profile);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{userId}")
  @Operation(summary = "User 삭제")
  public ResponseEntity<?> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "전체 User 목록 조회")
  public ResponseEntity<?> findAll() {
    List<UserDto.Response> responses = userService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }

  @PatchMapping("/{userId}/userStatus")
  @Operation(summary = "User 온라인 상태 업데이트")
  public ResponseEntity<?> updateOnline(
      @PathVariable UUID userId,
      @RequestBody UserStatusDto.Update request) {
    UserStatusDto.Response response = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
