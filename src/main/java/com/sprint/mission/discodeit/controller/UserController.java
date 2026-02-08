package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final BinaryContentRepository binaryContentRepository;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto.response> createUser(@RequestPart("user") UserDto.createRequest userReq,
                                                       @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        binary


        var k = new BinaryContentDto.createRequest(BinaryContentType.FILE, )


//        return new ResponseEntity<>()

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userReq, profileReq));
    }
}
