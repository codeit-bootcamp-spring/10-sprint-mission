package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.user.ProfileImageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.binarycontent.FileIOException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final BinaryContentService binaryContentService;

  public UserController(
      UserService userService,
      BinaryContentService binaryContentService
  ) {
    this.userService = userService;
    this.binaryContentService = binaryContentService;
  }

  @Operation(summary = "전체 User 목록 조회", operationId = "findAll", tags = {"User"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
  })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAllDto());
  }

  @Operation(summary = "User 등록", operationId = "create", tags = {"User"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User가 성공적으로 생성됨"),
      @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "User with email {email} already exists")
          )
      )
  })
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> create(
      @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
      @Parameter(description = "User 프로필 이미지")
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    UserCreateRequest createRequest = request;

    if (profile != null && !profile.isEmpty()) {
      try {
        ProfileImageCreateRequest image = new ProfileImageCreateRequest(
            profile.getOriginalFilename(),
            profile.getContentType(),
            profile.getBytes()
        );

        createRequest = new UserCreateRequest(
            request.userName(),
            request.email(),
            request.password(),
            image
        );
      } catch (IOException e) {
        throw new FileIOException();
      }
    }

    UserResponse created = userService.create(createRequest);
    log.info("사용자가 성공적으로 생성되었습니다. id = {}", created.id());

    return ResponseEntity.ok().body(toUserDto(created.id()));
  }

  @Operation(summary = "User 정보 수정", operationId = "update", tags = {"User"})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨"),
      @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "user with email {newEmail} already exists")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "User with id {userId} not found")
          )
      )
  })
  @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> update(
      @Parameter(description = "수정할 User ID")
      @PathVariable UUID userId,
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @Parameter(description = "수정할 User 프로필 이미지")
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
    UserDto userDto = toUserDto(updated.id());

    log.info("사용자정보가 성공적으로 수정되었습니다. id = {}", updated.id());
    return ResponseEntity.ok(userDto);
  }

  @Operation(summary = "User 삭제", operationId = "delete", tags = {"User"})
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "User with id {id} not found")
          )
      )
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
  public void delete(
      @Parameter(description = "삭제할 User ID")
      @PathVariable UUID userId
  ) {
    userService.delete(userId);
    log.info("사용자가 정상적으로 삭제되었습니다. id = {}", userId);
  }

  private UserDto toUserDto(UUID userId) {
    UserResponse user = userService.find(userId);

    BinaryContentDto profile = null;
    if (user.profileImageId() != null) {
      BinaryContentResponse binary = binaryContentService.find(user.profileImageId());

      profile = new BinaryContentDto(
          binary.id(),
          binary.fileName(),
          binary.size(),
          binary.contentType()
      );
    }

    return new UserDto(
        user.id(),
        user.userName(),
        user.email(),
        profile,
        user.online(),
        user.role()
    );
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
      throw new FileIOException();
    }
  }
}