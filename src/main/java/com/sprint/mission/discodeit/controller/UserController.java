package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "User")
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 등록
    @Operation(summary = "User 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto.userResponse> createUser(@Parameter(content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.userCreateRequest.class)))
                                                           @RequestPart("userCreateRequest") @Valid UserDto.userCreateRequest userReq,
                                                           @RequestPart(value = "profile", required = false) MultipartFile profileImage) throws IOException {
        BinaryContentDto.binaryContentCreateRequest profileReq = toServiceDto(profileImage);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userReq, profileReq));
    }

//    // 사용자 단일 조회(UUID)
//    @RequestMapping(value = "/{user-id}", method = RequestMethod.GET)
//    public ResponseEntity<UserDto.userResponse> findUser(@PathVariable("user-id") UUID userId) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(userService.findUser(userId));
//    }

//    // 사용자 단일 조회(Username)
//    @RequestMapping(params = "username", method = RequestMethod.GET)
//    public ResponseEntity<UserDto.userResponse> findUserByUsername(@RequestParam String username) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(userService.findUserByUsername(username));
//    }

//    // 사용자 단일 조회(Mail)
//    @RequestMapping(params = "email", method = RequestMethod.GET)
//    public ResponseEntity<UserDto.userResponse> findUserByMail(@RequestParam String email) {
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(userService.findUserByEmail(email));
//    }

    // 사용자 다중 조회
    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto.userResponse>> findUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findAllUsers());
    }

    // 사용자 수정
    @Operation(summary = "User 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "400", description = "User를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{user-id}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto.userResponse> updateUser(@PathVariable("user-id") UUID userId,
                                                           @Parameter(content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.userUpdateRequest.class)))
                                                           @RequestPart("userUpdateRequest") @Valid UserDto.userUpdateRequest userReq,
                                                           @RequestPart(value = "profile", required = false) MultipartFile profileImage) throws IOException {
        BinaryContentDto.binaryContentCreateRequest profileReq = toServiceDto(profileImage);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateUser(userId, userReq, profileReq));
    }

    // 사용자 온라인 상태 업데이트
    @Operation(summary = "User 온라인 상태 업데이트")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨"),
            @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{user-id}/userStatus", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatusDto.userStatusResponse> updateLastActive(@PathVariable("user-id") UUID userId,
                                                                             @RequestBody UserStatusDto.userStatusUpdateRequest updateReq) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userStatusService.updateUserStatusByUserId(userId, updateReq));
    }

    // 사용자 삭제
    @Operation(summary = "User 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{user-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private BinaryContentDto.binaryContentCreateRequest toServiceDto(MultipartFile profileImage) throws IOException {
        if (profileImage == null) return null;

        return new BinaryContentDto.binaryContentCreateRequest(BinaryContentType.fromMimeType(profileImage.getContentType()),
                profileImage.getOriginalFilename(), profileImage.getBytes());
    }
}
