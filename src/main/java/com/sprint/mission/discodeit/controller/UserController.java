package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final UserMapper userMapper;
    private final UserStatusMapper userStatusMapper;

    @Operation(summary = "User 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> create(@RequestPart(value = "userCreateRequest") UserCreateRequest userCreateRequest,
                                          @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException { //파라미터 이름으로 추론안하게 @RequestPart 이름 명시해줄것.
        if(profile != null) {
            BinaryContentCreateRequest profileImage = new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            );

            userCreateRequest = new UserCreateRequest(
                    userCreateRequest.username(),
                    userCreateRequest.email(),
                    userCreateRequest.password(),
                    Optional.of(profileImage)
            );
        }

        UserDto userDto = userService.create(userCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") UUID userId) {
        UserDto userDto = userService.find(userId);
        return ResponseEntity.ok(userDto);
    }


    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "사용자 수정")
    @PatchMapping(value = "/{userId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> updateUser(@PathVariable("userId") UUID userId,
                           @RequestPart UserUpdateRequest userUpdateRequest,
                           @RequestPart(required = false) MultipartFile profile) throws IOException {

        if(profile != null) {
            BinaryContentCreateRequest profileImage = new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            );

            userUpdateRequest = new UserUpdateRequest(
                    userUpdateRequest.newUsername(),
                    userUpdateRequest.newEmail(),
                    userUpdateRequest.newPassword(),
                    Optional.of(profileImage)
            );

        }

        UserDto userDto = userService.update(userId,userUpdateRequest);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusDto> updateOnline(@PathVariable UUID userId,
                                                      @RequestBody UserStatusUpdateRequest userStatusUpdateRequest){

        userStatusService.update(userId, userStatusUpdateRequest);
        UserStatus userStatus = userStatusService.findByUserId(userId);
        UserStatusDto userStatusDto = userStatusMapper.toDto(userStatus);
        return ResponseEntity.ok(userStatusDto);

    }

    @Operation(summary = "User 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "User with id {id} not found")
                    )
            )
    })
    @DeleteMapping("/{userId}")

    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();//204
    }
}
