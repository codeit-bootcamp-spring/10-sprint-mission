package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "1. 사용자 및 인증 그룹", description = "사용자 계정 생성, 수정, 삭제 및 상태 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @Operation(summary = "유저 등록", description = "새로운 유저 정보를 저장하고 프로필 이미지를 업로드합니다.")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto create(
      @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
      @Parameter(description = "유저 프로필 이미지 파일 (선택 사항)")
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    log.info("[REQUEST] Create User: email={}, name={}", request.getEmail(),
        request.getUsername());

    return userService.create(request, profile);
  }

  @Operation(summary = "유저 정보 수정", description = "특정 유저의 닉네임, 이메일, 비밀번호 및 프로필 이미지를 수정합니다.")
  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public UserDto update(
      @Parameter(description = "수정할 유저의 UUID", required = true)
      @PathVariable UUID userId,
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @Parameter(description = "새로운 프로필 이미지 파일 (선택 사항)")
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    log.info("[REQUEST] Update User: id={}", userId);

    return userService.update(userId, request, profile);
  }

  @Operation(summary = "유저 삭제", description = "시스템에서 유저 정보를 영구적으로 삭제합니다.")
  @DeleteMapping("/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @Parameter(description = "삭제할 유저의 UUID", required = true)
      @PathVariable UUID userId) {

    log.info("[REQUEST] Delete User: id={}", userId);

    userService.delete(userId);
  }

  @Operation(summary = "모든 유저 조회", description = "시스템에 등록된 전체 유저 리스트를 가져옵니다.")
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAllUsers());
  }
}
