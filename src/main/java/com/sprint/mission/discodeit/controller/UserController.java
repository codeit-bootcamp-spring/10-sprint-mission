package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelWithLastMessageDTO;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ChannelService channelService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createUser(
            @RequestBody CreateUserRequestDTO dto
            ) {
        UserResponseDTO created = userService.createUser(dto);

        // 현재 요청 URL(/v1/users)을 기준으로
        // 새로 생성된 사용자 리소스의 주소(/v1/users/{id})를 만들어줌
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()       // POST /v1/users
                .path("/{id}")              // -> /v1/users/{id}
                .buildAndExpand(created.userId())       // {id}에 실제 생성된 userId 삽입
                .toUri();       // URI 객체로 변환(Location 헤더용)

        return ResponseEntity.created(location)
                .body(created);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity updateUser(
            @PathVariable UUID userId,
            @RequestBody UpdateUserRequestDTO dto
            ) {
        UserResponseDTO updated = userService.updateUser(userId, dto);

        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    public ResponseEntity updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody UpdateUserRequestDTO dto
    ) {
        UserResponseDTO updated = userService.updateUser(userId, dto);

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
                        200,
                        "사용자가 삭제되었습니다."
                )
        );
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAll() {
        List<UserResponseDTO> users = userService.findAll();

        return ResponseEntity.ok(users);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity findByUserId(
            @PathVariable UUID userId
    ) {
        UserResponseDTO response = userService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{userId}/channels", method = RequestMethod.GET)
    public ResponseEntity findAllChannelsByUserId(
            @PathVariable UUID userId
    ) {
        List<ChannelWithLastMessageDTO> channels = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channels);
    }
}
