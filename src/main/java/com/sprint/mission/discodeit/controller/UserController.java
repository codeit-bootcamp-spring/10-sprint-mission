package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "User 등록", description = "User를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = {
                            @Encoding(name = "userCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE),
                            @Encoding(name = "profile", contentType = "image/*")
                    }
            )
    )
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestPart(value = "userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        log.info("사용자 생성 요청: username={}, email={}", userCreateRequest.username(), userCreateRequest.email());
        BinaryContentRequest profileImage = new BinaryContentRequest(BinaryContentOwnerType.USER,
                profile);

        UserDto response = userService.createUser(userCreateRequest, profileImage);
        log.info("사용자 생성 완료: userId={}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "User 단건 조회", description = "userId로 User를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User 조회 성공"),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
    })
    public ResponseEntity<UserDto> findUserByUserId(
            @Parameter(description = "조회할 User ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
            @PathVariable UUID userId
    ) {
        log.debug("사용자 단건 조회 요청: userId={}", userId);
        UserDto response = userService.findUserByUserID(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "전체 User 목록 조회", description = "전체 User 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
    public ResponseEntity<List<UserDto>> findAllUsers() {
        log.debug("전체 사용자 목록 조회 요청");
        List<UserDto> responses = userService.findAllUsers();

        return ResponseEntity.ok(responses);
    }

    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "User 정보 수정", description = "User 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = {
                            @Encoding(name = "userUpdateRequest", contentType = MediaType.APPLICATION_JSON_VALUE),
                            @Encoding(name = "profile", contentType = "image/*")
                    }
            )
    )
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "수정할 User ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        log.info("사용자 수정 요청: userId={}", userId);
        BinaryContentRequest profileImage = new BinaryContentRequest(BinaryContentOwnerType.USER,
                profile);

        UserDto response = userService.updateUser(userId, userUpdateRequest, profileImage);
        log.info("사용자 수정 완료: userId={}", userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "User 삭제", description = "User를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "삭제할 User ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
            @PathVariable UUID userId
    ) {
        log.info("사용자 삭제 요청: userId={}", userId);
        userService.deleteUser(userId);
        log.info("사용자 삭제 완료: userId={}", userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/userStatus")
    @Operation(summary = "User 온라인 상태 업데이트", description = "User의 온라인 상태를 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨"),
            @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음")
    })
    public ResponseEntity<UserStatusDto> updateUserStatus(
            @Parameter(description = "상태를 변경할 User ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        log.debug("사용자 상태 업데이트 요청: userId={}", userId);
        UserStatusDto response = userStatusService.updateUserStatusByUserId(userId, request);

        return ResponseEntity.ok(response);
    }
}
