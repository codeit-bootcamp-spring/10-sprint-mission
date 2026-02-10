package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

    public final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(method = RequestMethod.POST, path = "/public")
    public ResponseEntity<ChannelDto.ChannelResponsePublic> createPublicChannel(
            @RequestBody ChannelDto.ChannelRequest request
    ) {
        ChannelDto.ChannelResponsePublic channelData = channelService.createPublic(request);
        return new ResponseEntity<>(channelData, HttpStatus.CREATED);
    }

    // 비공개 채널 생성
    @RequestMapping(method = RequestMethod.POST, path = "/private")
    public ResponseEntity<ChannelDto.ChannelResponsePrivate> createPrivateChannel(
            @RequestBody List<UUID> userIds
    ) {
        ChannelDto.ChannelResponsePrivate channelData = channelService.createPrivate(userIds);
        return new ResponseEntity<>(channelData, HttpStatus.CREATED);
    }

    // 공개 채널 수정
    @RequestMapping(method = RequestMethod.PUT, path = "/{channelId}")
    public ResponseEntity<ChannelDto.ChannelResponsePublic> updatePublicChannel(
            @PathVariable UUID channelId,
            @RequestBody ChannelDto.ChannelRequest request
    ) {
        ChannelDto.ChannelResponsePublic updated = channelService.update(channelId, request);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // 채널 삭제
    @RequestMapping(method = RequestMethod.DELETE, path = "/{channelId}")
    public ResponseEntity<Void> deleteChannel(
            @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    // 사용자별 채널 조회
    @RequestMapping(method = RequestMethod.GET, path = "")
    public ResponseEntity<List<ChannelDto.ChannelResponse>> findAllChannelsByUser(
            @RequestParam UUID userId
    ) {
        List<ChannelDto.ChannelResponse> channels = channelService.findAllByUserId(userId);
        return new ResponseEntity<>(channels, HttpStatus.OK);
    }

    // 채널 조회
    @RequestMapping(method = RequestMethod.GET, path = "/{channelId}")
    public ResponseEntity<ChannelDto.ChannelResponse> findChannelById(
            @PathVariable UUID channelId
    ) {
        ChannelDto.ChannelResponse channel = channelService.findById(channelId);
        return new ResponseEntity<>(channel, HttpStatus.OK);
    }

}
