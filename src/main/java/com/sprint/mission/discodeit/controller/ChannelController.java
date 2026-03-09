package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.dto.user.UserDto;
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
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final UserService userService;
    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity createPublicChannel(
            @RequestBody CreatePublicChannelRequestDTO dto
    ) {
        ChannelDto created = channelService.createPublicChannel(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location)
                .body(created);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity createPrivateChannel(
            @RequestBody CreatePrivateChannelRequestDTO dto
            ) {
        ChannelDto created = channelService.createPrivateChannel(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location)
                .body(created);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity updateChannel(
            @PathVariable UUID channelId,
            @RequestBody UpdateChannelRequestDTO dto
            ) {
        ChannelDto response = channelService.updateChannel(channelId, dto);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteChannel(
            @PathVariable UUID channelId
    ) {
        channelService.deleteChannel(channelId);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findChannelsByUserId(
            @RequestParam("userId") UUID userId
    ) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channels);
    }
}
