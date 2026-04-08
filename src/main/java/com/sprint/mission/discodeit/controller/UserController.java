package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity createUser(
            @RequestPart("userCreateRequest") @Valid CreateUserRequestDTO dto,
            @RequestPart(value = "profile", required = false) MultipartFile profile
            ) {
        log.debug("[USER_CREATE_REQUEST] 유저 생성 요청: username={}", dto.username());
        CreateBinaryContentPayloadDTO payload = null;

        if (profile != null && !profile.isEmpty()) {
            try {
                payload = new CreateBinaryContentPayloadDTO(
                        profile.getBytes(),
                        profile.getContentType(),
                        profile.getOriginalFilename(),
                        profile.getSize()
                );
            } catch (IOException e) {
                log.warn("[USER_CREATE_REQUEST_FAIL] 프로필 파일 읽기 실패로 유저 생성 요청 실패: username={}", dto.username());
                throw new BinaryContentException(
                        ErrorCode.BINARY_CONTENT_CAN_NOT_READ,
                        Map.of("username", dto.username(), "filename", profile.getOriginalFilename())
                );
            }
        }

        UserDto created = userService.createUser(dto, payload);

        // 현재 요청 URL(/v1/users)을 기준으로
        // 새로 생성된 사용자 리소스의 주소(/v1/users/{id})를 만들어줌
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()       // POST /v1/users
                .path("/{id}")              // -> /v1/users/{id}
                .buildAndExpand(created.id())       // {id}에 실제 생성된 userId 삽입
                .toUri();       // URI 객체로 변환(Location 헤더용)

        return ResponseEntity.created(location)
                .body(created);
    }

    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity updateUser(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") @Valid UpdateUserRequestDTO dto,
            @RequestPart(value = "profile", required = false) MultipartFile profile
            ) {
        log.debug("[USER_UPDATE_REQUEST] 유저 정보 수정 요청: userId={}", userId);
        CreateBinaryContentPayloadDTO payload = null;

        if (profile != null && !profile.isEmpty()) {
            try {
                payload = new CreateBinaryContentPayloadDTO(
                        profile.getBytes(),
                        profile.getContentType(),
                        profile.getOriginalFilename(),
                        profile.getSize()
                );
            } catch (IOException e) {
                log.warn("[USER_UPDATE_REQUEST_FAIL] 프로필 파일 읽기 실패로 유저 정보 수정 요청 실패: userId={}", userId);
                throw new BinaryContentException(
                        ErrorCode.BINARY_CONTENT_CAN_NOT_READ,
                        Map.of("userId", userId, "filename", profile.getOriginalFilename())
                );
            }
        }

        UserDto updated = userService.updateUserInfo(userId, dto, payload);

        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
    public ResponseEntity updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody @Valid UpdateUserStatusRequestDTO dto
    ) {
        log.debug("[USER_STATUS_UPDATE_REQUEST] 유저 상태 수정 요청: userId={}", userId);
        UserDto updated = userService.updateUserStatus(userId, dto);

        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(
            @PathVariable UUID userId
            ) {
        log.debug("[USER_DELETE_REQUEST] 유저 삭제 요청: userId={}", userId);
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAll() {
        List<UserDto> users = userService.findAll();

        return ResponseEntity.ok(users);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity findByUserId(
            @PathVariable UUID userId
    ) {
        UserDto response = userService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }
}
