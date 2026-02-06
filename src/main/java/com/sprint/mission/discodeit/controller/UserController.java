package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.binarycontent.BinaryContentResponseMapper;
import com.sprint.mission.discodeit.mapper.user.UserResponseMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final UserResponseMapper userResponseMapper;
    private final BinaryContentResponseMapper binaryContentResponseMapper;
    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.POST)
    public UserResponseDto postUser(
        @RequestParam String username,
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam MultipartFile profileImage
    ) throws IOException {
        if(profileImage == null){
            return userService.create(new UserCreateRequestDto(username, email, password, null));
        }
        else{
            binaryContentService.create(new BinaryContentCreateRequestDto(profileImage.getBytes()));
            List<byte[]> file = new ArrayList<>();
            file.add(profileImage.getBytes());

            return userService.create(new UserCreateRequestDto(username,
                    email,
                    password,
                    new BinaryContentRequestDto(file)));
        }
    }



}
