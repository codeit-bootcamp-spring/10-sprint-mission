package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.binarycontent.BinaryContentResponseMapper;
import com.sprint.mission.discodeit.mapper.user.UserResponseMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final UserResponseMapper userResponseMapper;
    private final BinaryContentResponseMapper binaryContentResponseMapper;
    private final BinaryContentService binaryContentService;

    //사용자 등록
    @RequestMapping(method = RequestMethod.POST)
    public UserResponseDto postUser(
        @RequestBody UserCreateRequestDto dto,
        @RequestParam MultipartFile profileImage
    ) throws IOException {
        return userService.create(dto, profileImage);
    }

    //사용자 정보 수정
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public UserResponseDto patchUser(
            @PathVariable UUID id,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam MultipartFile profileImage
    ) throws IOException {
        if(profileImage == null){
            return userService.update(new UserUpdateRequestDto(id, username, email, password, null));
        }
        else{
            binaryContentService.create(new BinaryContentCreateRequestDto(profileImage.getBytes()));
            List<byte[]> file = new ArrayList<>();
            file.add(profileImage.getBytes());

            return userService.update(new UserUpdateRequestDto(
                    id,
                    username,
                    email,
                    password,
                    new BinaryContentRequestDto(file)));
        }
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
