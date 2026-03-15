package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operation(summary = "User 등록")
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨",
                  content = @Content(mediaType = "application.json",
                          schema = @Schema(implementation = UserDto.class)
                  )
          ),
          @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                  content = @Content(
                          examples = @ExampleObject(value = "User with email {email} already exists")
                  )
          )
  })
  public ResponseEntity<User> createUser(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdUser);
  }



  @RequestMapping(
      path = "/{userId}", method = RequestMethod.PATCH,
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  @Operation(summary = "User 정보 수정")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨",
                  content = @Content(mediaType = "application.json",
                          schema = @Schema(implementation = UserDto.class)
                  )
          ),
          @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                  content = @Content(
                          examples = @ExampleObject(value = "user with email {newEmail} already exists")
                  )
          ),
          @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "User with id {userId} not found")
                  )
          ),
  })
  public ResponseEntity<User> updateUser(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUser);
  }




  @RequestMapping(path = "/{userId}", method = RequestMethod.DELETE)
  @Operation(summary = "User 삭제")
  @ApiResponses({
          @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
          @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "User with id {id} not found")
                  )
          )
  })
  public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }



  @RequestMapping(method = RequestMethod.GET)
  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(
          responseCode = "200", description = "User 목록 조회 성공",
          content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserDto.class)
          )
  )
  public ResponseEntity<List<UserDto>> findUsers() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }





  @RequestMapping(path = "/{userId}/userStatus", method = RequestMethod.PATCH)
  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
                  content = @Content(mediaType = "application.json",
                          schema = @Schema(implementation = UserDto.class)
                  )
          ),
          @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
                  content = @Content(
                          examples = @ExampleObject(value = "UserStatus with userId {userId} not found")
                  )
          )
  })
  public ResponseEntity<UserStatus> updateUserStatus(@PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request) {
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUserStatus);
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
