package com.sprint.mission.discodeit.user.controller;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.user.dto.*;
import com.sprint.mission.discodeit.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "User")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;

  @Operation(summary = "User 등록",
      operationId = "create")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201",
          description = "User가 성공적으로 생성됨"),
      @ApiResponse(responseCode = "400",
          description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              examples = @ExampleObject(value = "User with email {email} already exists")))
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> createUser(
      @Parameter(description = "User 생성 정보")
      @Valid @RequestPart UserCreateRequest userCreateRequest,
      @Parameter(description = "User 프로필 이미지")
      @RequestPart(required = false) MultipartFile profile) {

    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto createdUser = userService.create(userCreateRequest, profileRequest);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(createdUser);
  }

  @Operation(summary = "User 정보 수정",
      operationId = "update")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User 정보가 성공적으로 수정됨"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              examples = @ExampleObject(value = "user with email {newEmail} already exists")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(value = "User with id {userId} not found")
          )
      )
  })
  @PatchMapping(
      value = "/{userId}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> updateUser(
      @Parameter(description = "수정할 User ID")
      @PathVariable UUID userId,
      @Parameter(description = "수정할 User 정보")
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @Parameter(description = "수정할 User 프로필 이미지")
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUser);
  }

  @Operation(summary = "User 삭제",
      operationId = "delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204",
          description = "User가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              examples = @ExampleObject(value = "User with id {id} not found")
          ))
  })
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "삭제할 User ID")
      @PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "전체 User 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User 목록 조회 성공"
      )
  })
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> findById(@PathVariable UUID userId) {
    UserDto userDto = userService.find(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profileFile.getOriginalFilename(),
            profileFile.getContentType(),
            profileFile.getBytes()
        );
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
