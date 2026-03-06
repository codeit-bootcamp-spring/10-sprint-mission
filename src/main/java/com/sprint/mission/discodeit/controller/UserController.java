package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
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
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;
    
    // 사용자 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "User 등록")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User가 성공적으로 생성됨",
                    content = @Content(
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(
                            examples = @ExampleObject(value = "이미 동일한 username을 갖고 있는 유저가 있습니다")
                    )
            )
    })
    public ResponseEntity<UserDto> create(@Valid @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
                                                         @Parameter(description = "User 프로필 이미지", required = false)
                                                         @RequestPart(value = "profile", required = false) MultipartFile profileImage) {
        Optional<BinaryContentCreateRequest> profileImageDto = toBinaryContentCreateRequest(profileImage);
        UserDto response = userService.create(userCreateRequest, profileImageDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // 사용자 정보 수정
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "User 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User 정보가 성공적으로 수정됨",
                    content = @Content(
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(
                            examples = @ExampleObject(value = "수정하려는 새로운 username 또는 email를 사용중인 유저가 이미 있습니다")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "userId: {userId} 를 가진 user를 찾지 못했습니다")
                    )
            )
    })
    public ResponseEntity<UserDto> update(
            @Parameter(description = "수정할 User ID")
            @PathVariable("userId") UUID userId,
            @Parameter(description = "수정할 User 정보")
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @Parameter(description = "수정할 User 프로필 이미지")
            @RequestPart(value = "profile", required = false) MultipartFile profileImage) {
        Optional<BinaryContentCreateRequest> profileImageDto = toBinaryContentCreateRequest(profileImage);
        UserDto response = userService.update(userId, userUpdateRequest, profileImageDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 사용자 정보 삭제
    @DeleteMapping(value = "/{userId}")
    @Operation(summary = "User 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User가 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "userId: {userId} 를 가진 user를 찾지 못했습니다")
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 User ID")
            @PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    // 특정 사용자 조회
    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserDto> find(@PathVariable("userId") UUID userId) {
        UserDto response = userService.find(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // 모든 사용자 조회
    @GetMapping
    @Operation(summary = "전체 User 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User 목록 조회 성공",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    )
            )
    })
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> response = userService.findAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 사용자 온라인 상태 업데이트 -> UserStatusService를 필드로?
    @PatchMapping(value = "/{userId}/userStatus")
    @Operation(summary = "User 온라인 상태 업데이트")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User 온라인 상태가 성공적으로 업데이트됨",
                    content = @Content(
                            schema = @Schema(implementation = UserStatusDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "userId: {userId}를 가진 UserStatus를 찾지 못했습니다")
                    )
            )
    })
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
            @Parameter(description = "상태를 변경할 User ID")
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatusDto response = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 유저 생성, 수정 시 입력 받은 프로필 이미지를 Service에 전달하기 전 Optional<BinaryContentCreateRequest>로 변환하는 private 메서드
    private Optional<BinaryContentCreateRequest> toBinaryContentCreateRequest(MultipartFile file) {
        return Optional.ofNullable(file)
                .map(f -> {
                    try {
                        return new BinaryContentCreateRequest(
                                f.getOriginalFilename(),
                                f.getBytes(),
                                f.getContentType()
                        );
                    } catch (IOException e) {
                        throw new IllegalArgumentException("파일 처리 중 오류 발생" + e.getMessage());
                    }
                });
    }
}
