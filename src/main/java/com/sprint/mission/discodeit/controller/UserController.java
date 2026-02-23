package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

    @Operation(summary = "User 등록")
    @ApiResponse(
            responseCode = "201",
            description = "User가 성공적으로 생성됨"
            )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> createUser(@RequestPart UserCreateRequest userCreateRequest,
                                     @RequestPart(required = false) MultipartFile profile) throws IOException {
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

        User user = userService.create(userCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


    @GetMapping
    public ResponseEntity<List<UserDto>> findAllUser(Model model) {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "사용자 수정")
    @PatchMapping(value = "/{userId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateUser(@PathVariable("userId") UUID userId,
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

        User user = userService.update(userId,userUpdateRequest);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatus> updateOnline(@PathVariable UUID userId,
                                   @RequestBody UserStatusUpdateRequest userStatusUpdateRequest){

        userStatusService.update(userId, userStatusUpdateRequest);
        UserStatus userStatus = userStatusService.findByUserId(userId);
        return ResponseEntity.ok(userStatus);

    }

    @Operation(summary = "User 삭제")
    @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();//204
    }
}
