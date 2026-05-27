package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User")
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  // 사용자 등록
  @Operation(summary = "User 등록")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> createUser(
      @Parameter(content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.UserCreateRequest.class)))
      @RequestPart("userCreateRequest") @Valid UserDto.UserCreateRequest userReq,
      @RequestPart(value = "profile", required = false) MultipartFile profileImage)
      throws IOException {
    boolean hasProfile = profileImage != null && !profileImage.isEmpty();
    log.info("[Controller] 유저 생성 요청: username={}, hasProfile={}", userReq.username(), hasProfile);

    UserDto dto = userService.createUser(userReq, profileImage);
    log.debug("[Controller] 유저 생성 응답 준비: id={}", dto.id());

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // 사용자 다중 조회
  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
  @GetMapping
  public ResponseEntity<List<UserDto>> findUsers() {
    List<UserDto> dto = userService.findAllUsers();
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  // 사용자 수정
  @Operation(summary = "User 정보 수정")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨"),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PatchMapping(value = "/{user-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> updateUser(@PathVariable("user-id") UUID userId,
      @Parameter(content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.UserUpdateRequest.class)))
      @RequestPart("userUpdateRequest") @Valid UserDto.UserUpdateRequest userReq,
      @RequestPart(value = "profile", required = false) MultipartFile profileImage)
      throws IOException {
    boolean hasProfile = profileImage != null && !profileImage.isEmpty();
    log.info("[Controller] 유저 수정 요청: id={}, newUsername={}, hasProfile={}",
        userId, userReq.newUsername(), hasProfile);

    UserDto dto = userService.updateUser(userId, userReq, profileImage);
    log.debug("[Controller] 유저 수정 응답 준비: id={}", dto.id());

    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  // 사용자 삭제
  @Operation(summary = "User 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{user-id}")
  public ResponseEntity<Void> deleteUser(@PathVariable("user-id") UUID userId) {
    log.info("[Controller] 유저 삭제 요청: id={}", userId);

    userService.deleteUser(userId);
    log.debug("[Controller] 유저 삭제 응답 준비: id={}", userId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
