package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Operation(summary = "User 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "User가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 400, \"error\": \"EMAIL_ALREADY_EXISTS\", "
                      + "\"message\": \"사용 중인 이메일입니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<UserDto> create(
      @Parameter(description = "User 생성 정보", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @Valid @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @Parameter(description = "User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @Operation(summary = "전체 User 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
      )
  })
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @Operation(summary = "User 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value = "{ \"status\": 404, \"error\": \"USER_NOT_FOUND\", "
                  + "\"message\": \"유저를 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 400, \"error\": \"EMAIL_ALREADY_EXISTS\", "
                      + "\"message\": \"사용 중인 이메일입니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @PatchMapping(
      path = "/{userId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public ResponseEntity<UserDto> update(
      @Parameter(description = "수정할 User ID") @PathVariable UUID userId,
      @Parameter(description = "수정할 User 정보", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @Parameter(description = "수정할 User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
          content = @Content(schema = @Schema(implementation = UserStatusDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value =
                  "{ \"status\": 404, \"error\": \"USER_STATUS_NOT_FOUND\", "
                      + "\"message\": \"유저 상태 정보를 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @PatchMapping(path = "/{userId}/userStatus")
  public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
      @Parameter(description = "상태를 변경할 User ID") @PathVariable UUID userId,
      @Parameter(description = "변경할 User 온라인 상태 정보") @Valid @RequestBody UserStatusUpdateRequest request) {
    UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
  }

  @Operation(summary = "User 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "User가 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(
              schema = @Schema(implementation = ErrorDto.class),
              examples = @ExampleObject(value = "{ \"status\": 404, \"error\": \"USER_NOT_FOUND\", "
                  + "\"message\": \"유저를 찾을 수 없습니다.\", \"time\": \"2026-02-23T05:23:49.657764500Z\" }")
          )
      )
  })
  @DeleteMapping(path = "/{userId}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 User ID") @PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
