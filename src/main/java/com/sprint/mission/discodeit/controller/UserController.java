package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserPatchDto;
import com.sprint.mission.discodeit.dto.UserPostDto;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User controller 입니다.")
public class UserController {

    private final UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "전체 User 목록 조회", operationId = "findAll")
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "User 등록", operationId = "create")
    public ResponseEntity<UserDto> createUser(
        @Valid @RequestPart("userCreateRequest") UserPostDto userPostDto,
        @Parameter(description = "User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile) {

        log.info("[USER_CREATE] 유저 생성 요청: email={}, username={}", userPostDto.getEmail(),
            userPostDto.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userService.create(userPostDto, profile));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @Operation(summary = "User 삭제", operationId = "delete")
    public ResponseEntity<?> deleteUser(
        @Parameter(name = "userId", description = "삭제할 User ID") @PathVariable UUID userId) {

        log.info("[USER_DELETE] 유저 삭제 요청: id={}", userId);

        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "User 정보 수정", operationId = "update")
    public ResponseEntity<UserDto> updateUser(
        @Parameter(name = "userId", description = "수정할 User ID") @PathVariable UUID userId,
        @Valid @RequestPart("userUpdateRequest") UserPatchDto userPatchDto,
        @Parameter(name = "profile", description = "수정할 User 프로필 이미지") @RequestPart(required = false) MultipartFile profile) {

        log.info("[USER_UPDATE] 유저 수정 요청: newEmail={}, newUsername={}", userPatchDto.newEmail(),
            userPatchDto.newUsername());

        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.updateUser(userId, userPatchDto, profile));
    }


}
