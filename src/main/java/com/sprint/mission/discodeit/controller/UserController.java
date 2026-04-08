package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
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
import java.util.UUID;

@Slf4j
@Tag(name = "User")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @Operation(summary = "User 등록", operationId = "create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(examples = @ExampleObject(value = "User with email {email} already exists")))
    })
    @RequestMapping(method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(
            @RequestPart("userCreateRequest") @Valid CreateUserRequestDto request,
            @Parameter(description = "User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {
        log.info("REST 요청 - User 등록 시작: username={}", request.username());

        // 파일이 전송되었다면 DTO로 변환하여 request 객체에 주입
        if (profile != null && !profile.isEmpty()) {
            BinaryContentDto profileImage = new BinaryContentDto(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getSize(),
                    profile.getBytes()
            );
            request = new CreateUserRequestDto(
                    request.username(),
                    request.email(),
                    request.password(),
                    profileImage
            );
        }

        return userService.create(request);
    }

    @Operation(summary = "전체 User 목록 조회", operationId = "findAll")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }


    @Operation(summary = "User 정보 수정", operationId = "update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(examples = @ExampleObject(value = "user with email {newEmail} already exists"))),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "User with id {userId} not found")))
    })
    @RequestMapping(method = RequestMethod.PATCH, value = "/{userId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserDto> update(
            @Parameter(description = "수정할 User ID") @PathVariable UUID userId,
            @Valid @RequestPart(value = "userUpdateRequest") UpdateUserRequestDto request,
            @Parameter(description = "수정할 User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {
        log.info("REST 요청 - User 정보 수정 시작: userId={}", userId);

        if (request == null) {
            request = new UpdateUserRequestDto(null, null, null, null);
        }

        // 파일이 전송되었다면 DTO로 변환하여 request 객체의 newProfileImage에 주입
        if (profile != null && !profile.isEmpty()) {
            BinaryContentDto newProfileImage = new BinaryContentDto(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getSize(),
                    profile.getBytes()
            );

            request = new UpdateUserRequestDto(
                    request.newUsername(),
                    request.newEmail(),
                    request.newPassword(),
                    newProfileImage
            );
        }
        return ResponseEntity.ok(userService.update(userId, request));
    }

    @Operation(summary = "User 삭제", operationId = "delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "User with id {id} not found")))
    })
    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "삭제할 User ID") @PathVariable UUID userId){
        log.warn("REST 요청 - User 삭제 시작: userId={}", userId);
        userService.delete(userId);
    }

    // 값을 직접 수정하지 않고 서버에서 시간을 Instant()로 불러와서 Post 사용
    // 멱등성 고려
    @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨"),
            @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "UserStatus with userId {userId} not found")))
    })
    @RequestMapping(method = RequestMethod.PATCH, value = "/{userId}/userStatus")
    public ResponseEntity<UserStatusDto> updateStatus(@Parameter(description = "상태를 변경할 User ID") @PathVariable UUID userId,
                                                      @Valid @RequestBody UpdateUserStatusRequestDto request){
        return ResponseEntity.ok(userStatusService.updateByUserId(userId, request));
    }

}
