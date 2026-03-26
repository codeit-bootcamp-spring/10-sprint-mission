package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Slf4j
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity createPublicChannel(
            @RequestBody @Valid CreatePublicChannelRequestDTO dto
    ) {
        log.debug("[PUBLIC_CHANNEL_CREATE_REQUEST] 공개 채널 생성 요청: channelName={}", dto.name());
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
            @RequestBody @Valid CreatePrivateChannelRequestDTO dto
            ) {
        log.debug("[PRIVATE_CHANNEL_CREATE_REQUEST] 비공개 채널 생성 요청: participantsNumber={}", dto.participantIds().size());
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
            @RequestBody @Valid UpdateChannelRequestDTO dto
            ) {
        log.debug("[CHANNEL_UPDATE_REQUEST] 채널 정보 수정 요청: channelId={}", channelId);
        ChannelDto response = channelService.updateChannel(channelId, dto);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteChannel(
            @PathVariable UUID channelId
    ) {
        log.debug("[CHANNEL_DELETE_REQUEST] 채널 삭제 요청: channelId={}", channelId);
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
