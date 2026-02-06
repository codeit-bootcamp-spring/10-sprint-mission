package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
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
@RequestMapping("/v1/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final UserService userService;
    private final ChannelService channelService;

    @RequestMapping(value = "/create/public", method = RequestMethod.POST)
    public ResponseEntity createPublicChannel(
            @RequestBody CreatePublicChannelRequestDTO dto
    ) {
        ChannelResponseDTO created = channelService.createPublicChannel(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.channelId())
                .toUri();

        return ResponseEntity.created(location)
                .body(created);
    }

    @RequestMapping(value = "/create/private", method = RequestMethod.POST)
    public ResponseEntity createPrivateChannel(
            @RequestBody CreatePrivateChannelRequestDTO dto
            ) {
        ChannelResponseDTO created = channelService.createPrivateChannel(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.channelId())
                .toUri();

        return ResponseEntity.created(location)
                .body(created);
    }

    @RequestMapping(value = "/update/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity updateChannel(
            @PathVariable UUID channelId,
            @RequestBody UpdateChannelRequestDTO dto
            ) {
        ChannelWithLastMessageDTO response = channelService.updateChannel(channelId, dto);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteChannel(
            @PathVariable UUID channelId
    ) {
        channelService.deleteChannel(channelId);

        return ResponseEntity.ok(
                new DeleteChannelResponseDTO(
                        Instant.now(),
                        200,
                        "채널이 삭제되었습니다."
                )
        );
    }

    // TODO..
    // 유저쪽에서 갱신을 해야하는데 유저서비스에서 channelRepository를 건드릴 수 없음(요구사항)
    // 마찬가지로 채널서비스에서도 userRepository를 건드릴 수 없음(요구사항)
    // 그래서 저장을 못해서 계속 빈 리스트가 옴
    @RequestMapping(value = "/{channelId}/users", method = RequestMethod.GET)
    public ResponseEntity findAllUsersByChannelId(
            @PathVariable UUID channelId
    ) {
        List<UserResponseDTO> users = userService.findAllByChannel(channelId);

        return ResponseEntity.ok(users);
    }

    @RequestMapping(value = "/{channelId}/join/{userId}", method = RequestMethod.POST)
    public ResponseEntity joinChannel(
            @PathVariable UUID channelId,
            @PathVariable UUID userId
    ) {
        channelService.joinChannel(channelId, userId);
        ChannelWithLastMessageDTO response = channelService.findByChannelId(channelId);

        return ResponseEntity.ok(response);
    }

    // TODO..
    // 채널 탈퇴 구현 필요
}
