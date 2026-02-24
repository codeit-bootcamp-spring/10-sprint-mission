package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "User 등록")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함")
  })
  public ResponseEntity<?> create(
      @Valid @RequestPart UserCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    UserResponse response = userService.create(request, profile);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "User 정보 수정")
  public ResponseEntity<?> update(
      @Parameter(description = "수정할 User ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
      @PathVariable UUID userId,
      @Valid @RequestPart UserUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    UserResponse response = userService.update(userId, request, profile);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{userId}")
  @Operation(summary = "User 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
  })
  public ResponseEntity<?> delete(
      @Parameter(description = "삭제할 User ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
      @PathVariable UUID userId
  ) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
  public ResponseEntity<?> findAll() {
    List<UserResponse> responses = userService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }

  @PatchMapping("/{userId}/userStatus")
  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses({
      @ApiResponse(responseCode = "202", description = "User 온라인 상태가 성공적으로 업데이트됨"),
      @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음")
  })
  public ResponseEntity<?> updateOnline(
      @Parameter(description = "업데이트할 User ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
      @PathVariable UUID userId,
      @Valid @RequestBody UserStatusUpdateRequest request) {
    UserStatusResponse response = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
