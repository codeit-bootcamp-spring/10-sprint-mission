package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity createUser(
            @RequestPart("userCreateRequest") CreateUserRequestDTO dto,
            @RequestPart(value = "profile", required = false) MultipartFile profile
            ) {
        CreateBinaryContentPayloadDTO payload = null;

        if (profile != null && !profile.isEmpty()) {
            try {
                payload = new CreateBinaryContentPayloadDTO(
                        profile.getBytes(),
                        profile.getContentType(),
                        profile.getOriginalFilename()
                );
            } catch (IOException e) {
                throw new IllegalArgumentException("프로필 파일을 읽을 수 없습니다.", e);
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
            @RequestPart("userUpdateRequest") UpdateUserRequestDTO dto,
            @RequestPart(value = "profile", required = false) MultipartFile profile
            ) {
        CreateBinaryContentPayloadDTO payload = null;

        if (profile != null && !profile.isEmpty()) {
            try {
                payload = new CreateBinaryContentPayloadDTO(
                        profile.getBytes(),
                        profile.getContentType(),
                        profile.getOriginalFilename()
                );
            } catch (IOException e) {
                throw new IllegalArgumentException("프로필 파일을 읽을 수 없습니다.", e);
            }
        }

        UserDto updated = userService.updateUserInfo(userId, dto, payload);

        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
    public ResponseEntity updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody UpdateUserStatusRequestDTO dto
    ) {
        UserDto updated = userService.updateUserStatus(userId, dto);

        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(
            @PathVariable UUID userId
            ) {
        userService.deleteUser(userId);

        return ResponseEntity.ok(
                new DeleteUserResponseDTO(
                        Instant.now(),
                        204,
                        "사용자가 삭제되었습니다."
                )
        );
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
