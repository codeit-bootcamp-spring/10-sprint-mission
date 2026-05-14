package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;


  //전체 사용자 조회
  @RequestMapping(method = RequestMethod.GET)
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "User 목록 조회 성공",
          content = @Content(
              array = @ArraySchema(
                  schema = @Schema(
                      implementation = UserDto.class
                  )
              )
          )
      )
  })
  public ResponseEntity<List<UserDto>> getUsers() {
    log.trace("[User] 컨트롤러에서 사용자 목록 전체 조회 요청 받음");
    return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
  }

  //사용자 등록 (회원가입)
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "User가 성공적으로 생성됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username을 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = "text/plain",
              schema = @Schema(type = "string"),
              examples = @ExampleObject("User with email {email} already exists")
          ))
  })
  public ResponseEntity<UserDto> create(
      @Valid @RequestPart("userCreateRequest") UserCreateRequestDTO userCreateRequestDTO,
      @RequestPart(value = "profile", required = false) MultipartFile profileImage)
      throws IOException {

    log.trace("[User] 컨트롤러에서 사용자 생성 요청 받음");

    BinaryContentDto profileSaved = null;

    if (profileImage != null && !profileImage.isEmpty()) {
      profileSaved = new BinaryContentDto(
          UUID.randomUUID(),
          profileImage.getOriginalFilename(),
          profileImage.getSize(),
          profileImage.getContentType(),
          profileImage.getBytes()
      );
    }

    UserDto response = userService.create(userCreateRequestDTO, profileSaved);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }


  @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "User가 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject("User with id {id} not found")
          )
      )
  })
  public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
    log.trace("[User] 컨트롤러에서 사용자 삭제 요청 받음");
    userService.delete(userId);
    log.info("[User] 컨트롤러에서 서비스 계층의 삭제 메소드가 수행된 것을 확인.");
    return (ResponseEntity.noContent().build());
  }

  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ApiResponses({
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject("User with id {userId} not found")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username을 사용하는 user가 이미 존재함",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject("같은 email 또는 username를 사용하는 User가 이미 존재함")
          )
      ),
      @ApiResponse(
          responseCode = "200",
          description = "User 정보가 성공적으로 수정됨",
          content = @Content(
              schema = @Schema(implementation = User.class)
          )
      )
  })
  public ResponseEntity<UserDto> updateUser(@PathVariable UUID userId,
      @Valid @RequestPart("userUpdateRequest") UserUpdateDTO req,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) throws IOException {

    log.trace("[User] 컨트롤러에서 사용자의 수정 요청을 받음.");

    log.trace("[User] 컨트롤러에서 MultiFile로 들어온 첨부 파일을 확인하고 BinaryContentDto 객체 생성 시도");

    BinaryContentDto profileDto = null;
    if (profile != null && !profile.isEmpty()) {
      log.debug("[User] profile 존재. BinaryContentDto 객체를 생성합니다.");
      profileDto = new BinaryContentDto(
          UUID.randomUUID(),
          profile.getOriginalFilename(),
          profile.getSize(),
          profile.getContentType(),
          profile.getBytes()
      );
      log.debug("[User] BinaryContentDto 정보: id={}", profileDto.id());
    }

    return new ResponseEntity<>(userService.update(userId, req, profileDto),
        HttpStatus.OK);
  }

  @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
  @ApiResponses({
      @ApiResponse(
          responseCode = "404",
          description = "해당 User의 UserStatus를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject("UserStatus with userId {userId} not found")
          )
      ),
      @ApiResponse(
          responseCode = "200",
          description = "User 온라인 상태가 성공적으로 업데이트됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserStatus.class)
          )
      )
  }
  )
  public ResponseEntity<UserStatusDto> updateUserOnline(
      @PathVariable UUID userId,
      @Valid @RequestBody UserStatusUpdateRequestDTO req) {
    log.trace("[User] 컨트롤러에서 유저의 활동 중 상태 수정 요청을 받음");
    return new ResponseEntity<>(userStatusService.activateUserOnline(userId, req),
        HttpStatus.OK);
  }


}
