package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
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
    @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨")
    @PostMapping
    public ResponseEntity createUser(@RequestPart UserCreateRequest dto,
                                     @RequestPart MultipartFile profile) throws IOException {
        BinaryContentCreateRequest profileImage = new BinaryContentCreateRequest(
                profile.getOriginalFilename(),
                profile.getContentType(),
                profile.getBytes()
        );

        dto = new UserCreateRequest(
                dto.username(),
                dto.email(),
                dto.password(),
                Optional.of(profileImage)
        );

        User user = userService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


    @GetMapping
    public ResponseEntity<List<UserDto>> findAllUser(Model model) {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "User 삭제")
    @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();//삭제후 돌려줄 body가 없다.
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable("userId") UUID userId,
                           @RequestPart UserUpdateRequest dto,
                           @RequestPart MultipartFile profileImage) throws IOException {

        BinaryContentCreateRequest profile = new BinaryContentCreateRequest(
                profileImage.getOriginalFilename(),
                profileImage.getContentType(),
                profileImage.getBytes()
        );

        dto = new UserUpdateRequest(
                dto.newUsername(),
                dto.newEmail(),
                dto.newPassword(),
                Optional.of(profile)
        );

        return userService.update(userId,dto);
    }
    @PatchMapping("/{userId}/userStatus")
    public UserStatus updateOnline(@PathVariable UUID userId,
                                   @RequestBody UserStatusUpdateRequest userStatusUpdateRequest){

        userStatusService.update(userId, userStatusUpdateRequest);
        return userStatusService.findByUserId(userId);

    }


}
