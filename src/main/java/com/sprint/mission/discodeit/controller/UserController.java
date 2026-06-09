package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
//import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {

  /*
   **사용자 관리**
      - [ ]  사용자를 등록할 수 있다.
      - [ ]  사용자 정보를 수정할 수 있다.
      - [ ]  사용자를 삭제할 수 있다.
      - [ ]  모든 사용자를 조회할 수 있다.
      - [ ]  사용자의 온라인 상태를 업데이트할 수 있다.
   */
  private final UserService userService;
  //private final UserStatusService userStatusService;
  private final BinaryContentMapper binaryContentMapper;

  @Operation(summary = "User 등록")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨"),
      @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = """
                      {
                        "fieldErrors": null,
                        "violationErrors": null,
                        "code": 400,
                        "message": "이미 존재하는 사용자이름"
                      }
                  """)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = """
                      {
                            "fieldErrors": [
                              {
                                "field": "email",
                                "rejectedValue": "user",
                                "message": "이메일 형식이 올바르지 않습니다."
                              },
                              {
                                "field": "username",
                                "rejectedValue": "",
                                "message": "사용자 이름을 입력해주세요."
                              }
                            ],
                            "violationErrors": null,
                            "code": null,
                            "message": null
                          }
                  """)
          )
      )
  })
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<UserDto> create(
      @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @Valid @RequestPart(value = "userCreateRequest") UserCreateRequest dto,
      @Parameter(description = "User 프로필 이미지")
      @RequestPart(value = "profile", required = false) MultipartFile multipartFile) {
    BinaryContentCreateDto binaryContentCreateDto = null;
    if (multipartFile != null && !multipartFile.isEmpty()) {
      binaryContentCreateDto = binaryContentMapper.toCreateDto(multipartFile);
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.create(dto, Optional.ofNullable(binaryContentCreateDto)));
  }

  @Operation(summary = "User 정보 수정", operationId = "update",
      parameters = @Parameter(name = "userId", description = "수정할 User ID")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨"),
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = """
                      {
                        "fieldErrors": null,
                        "violationErrors": null,
                        "code": 404,
                        "message": "존재하지 않는 사용자"
                      }
                  """)
          )
      ),
      @ApiResponse(
          responseCode = "409",
          description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = """
                      {
                        "fieldErrors": null,
                        "violationErrors": null,
                        "code": 409,
                        "message": "이미 존재하는 사용자이름"
                      }
                  """)
          )
      )
  })
  @PreAuthorize("#userId==principal.userDto.id")
  @PatchMapping(path = "/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<UserDto> updateUser(
      @PathVariable UUID userId,
      @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest dto,
      @Parameter(description = "수정할 User 프로필 이미지")
      @RequestPart(value = "profile", required = false) MultipartFile multipartFile) {
    BinaryContentCreateDto binaryContentCreateDto = null;
    if (multipartFile != null && !multipartFile.isEmpty()) {
      binaryContentCreateDto = binaryContentMapper.toCreateDto(multipartFile);
    }
    return ResponseEntity.ok(
        userService.update(userId, dto, Optional.ofNullable(binaryContentCreateDto)));
  }

  @Operation(
      summary = "User 삭제",
      operationId = "delete",
      parameters = @Parameter(name = "userId", description = "삭제할 User ID", required = true))
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = """
                      {
                        "fieldErrors": null,
                        "violationErrors": null,
                        "code": 404,
                        "message": "존재하지 않는 사용자"
                      }
                  """)
          )
      )
  })
  @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨")
  @DeleteMapping("/{userId}")
  @PreAuthorize("#userId==principal.userDto.id")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "전체 User 목록 조회")
  @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }

//  @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId",
//      parameters = @Parameter(name = "userId", description = "상태를 변경할 User ID", required = true))
//  @ApiResponses({
//      @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨"),
//      @ApiResponse(
//          responseCode = "404",
//          description = "해당 User의 UserStatus를 찾을 수 없음",
//          content = @Content(
//              mediaType = "application/json",
//              schema = @Schema(implementation = ErrorResponse.class),
//              examples = @ExampleObject(value = """
//                      {
//                        "fieldErrors": null,
//                        "violationErrors": null,
//                        "code": 404,
//                        "message": "존재하지 않는 사용자 상태 정보"
//                      }
//                  """)
//          )
//      )
//  })
//  @PatchMapping(path = "/{userId}/userStatus")
//  public ResponseEntity<UserStatusDto> updateUserStatus(@PathVariable UUID userId,
//      @Valid @RequestBody UserStatusUpdateDto dto) {
//    return ResponseEntity.ok(userStatusService.updateByUserId(userId, dto));
//  }

}
