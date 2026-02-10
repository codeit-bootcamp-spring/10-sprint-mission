package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public UserDto create(
            @RequestPart("request") @Valid CreateUserRequestDto request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        // 파일이 전송되었다면 DTO로 변환하여 request 객체에 주입
        if (file != null && !file.isEmpty()) {
            BinaryContentDto profileImage = new BinaryContentDto(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes()
            );
            request.setProfileImage(profileImage);
        }

        return userService.create(request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    public UserDto findOne(@PathVariable UUID userId){
        return userService.find(userId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findAll")
    public ResponseEntity<List<UserDto>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{userId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public UserDto update(
            @PathVariable UUID userId,
            @RequestPart(value = "request", required = false) UpdateUserRequestDto request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        if (request == null) {
            request = new UpdateUserRequestDto(null, null, null, null);
        }

        // 파일이 전송되었다면 DTO로 변환하여 request 객체의 newProfileImage에 주입
        if (file != null && !file.isEmpty()) {
            BinaryContentDto newProfileImage = new BinaryContentDto(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes()
            );

            request.setNewProfileImage(newProfileImage);
        }
        return userService.update(userId, request);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
    public void delete(@PathVariable UUID userId){
        userService.delete(userId);
    }

    // 값을 직접 수정하지 않고 서버에서 시간을 Instant()로 불러와서 Post 사용
    // 멱등성 고려
    @RequestMapping(method = RequestMethod.POST, value = "/{userId}/status")
    public UserStatusDto updateStatus(@PathVariable UUID userId){
        return userStatusService.updateByUserId(userId);
    }

}
