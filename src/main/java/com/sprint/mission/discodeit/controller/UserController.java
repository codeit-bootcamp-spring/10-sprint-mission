package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.ProfileImageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateMultipartRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserStatusResponse;
import com.sprint.mission.discodeit.dto.user.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  public UserController(UserService userService, UserStatusService userStatusService) {
    this.userService = userService;
    this.userStatusService = userStatusService;
  }

  // GET /api/users -> 200 + UserDto[]
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAllDto());
  }

  // POST /api/users (multipart) -> 201 + User
  @Operation(summary = "User 등록")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "400", description = "Bad Request"),
      @ApiResponse(responseCode = "409", description = "Conflict")
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      // 제공 스펙에 맞춰 docs 표현을 multipart schema로 고정
      required = false,
      content = @Content(
          mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
          schema = @Schema(implementation = UserCreateMultipartRequest.class)
      )
  )
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    UserCreateRequest request = userCreateRequest;

    if (profile != null && !profile.isEmpty()) {
      try {
        ProfileImageCreateRequest profileImage = new ProfileImageCreateRequest(
            profile.getOriginalFilename(),
            profile.getContentType(),
            profile.getBytes()
        );

        request = new UserCreateRequest(
            userCreateRequest.userName(),
            userCreateRequest.email(),
            userCreateRequest.password(),
            profileImage
        );
      } catch (IOException e) {
        throw new BusinessLogicException(ErrorCode.FILE_IO_ERROR);
      }
    }

    UserResponse created = userService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.findEntity(created.id()));
  }

  // PATCH /api/users/{userId} (multipart) -> 200 + User
  @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> update(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    UserUpdateRequest request = new UserUpdateRequest(
        userId,
        userUpdateRequest.userName(),
        userUpdateRequest.email(),
        userUpdateRequest.password(),
        Optional.ofNullable(readProfile(profile))
    );

    UserResponse updated = userService.update(request);
    return ResponseEntity.ok(userService.findEntity(updated.id()));
  }

  // DELETE /api/users/{userId} -> 204
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "No Content"),
      @ApiResponse(responseCode = "404", description = "Not Found")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
  public void delete(@PathVariable UUID userId) {
    userService.delete(userId);
  }

  // PATCH /api/users/{userId}/userStatus -> 200 + UserStatusResponse
  @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
  public ResponseEntity<UserStatusResponse> updateUserStatusByUserId(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request
  ) {
    return ResponseEntity.ok(userStatusService.updateByUserId(userId, request.newLastActiveAt()));
  }

  private ProfileImageCreateRequest readProfile(MultipartFile profile) {
    if (profile == null || profile.isEmpty()) {
      return null;
    }
    try {
      return new ProfileImageCreateRequest(
          profile.getOriginalFilename(),
          profile.getContentType(),
          profile.getBytes()
      );
    } catch (IOException e) {
      throw new BusinessLogicException(ErrorCode.FILE_IO_ERROR);
    }
  }
}