package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.mapper.binarycontent.BinaryContentResponseMapper;
import com.sprint.mission.discodeit.mapper.user.UserResponseMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    //사용자 등록
    @RequestMapping(method = RequestMethod.POST)
    public UserResponseDto postUser(
            @RequestPart("dto") UserCreateRequestDto dto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws IOException {
        //영속화
        if(profileImage != null && !profileImage.isEmpty()){
            String fileName = profileImage.getOriginalFilename();
            Path savePath = Paths.get("./upload/" + fileName);
            Files.createDirectories(savePath.getParent());
            profileImage.transferTo(savePath);
        }
        return userService.create(dto, profileImage);
    }

    //사용자 정보 수정
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public UserResponseDto patchUser(
            @PathVariable UUID id,
            @RequestPart("dto") UserCreateRequestDto dto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws IOException {
        if(profileImage != null && !profileImage.isEmpty()){
            String fileName = profileImage.getOriginalFilename();
            Path savePath = Paths.get("./upload/" + fileName);
            Files.createDirectories(savePath.getParent());
            profileImage.transferTo(savePath);
        }

        return userService.update(new UserUpdateRequestDto(id, dto.username(), dto.email(), dto.password()), profileImage);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable UUID id) {
        userService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserResponseDto> getAllUser(){
        return userService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void updateUserStatus(@PathVariable UUID id){
        userStatusService.updateByUserId(new UserStatusUpdateRequestDto(true, id, Instant.now()));
    }
}
