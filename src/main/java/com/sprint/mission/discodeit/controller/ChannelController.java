package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/channels")
public class ChannelController {
    private final ChannelService channelService;

    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    // 공개 채널을 생성할 수 있다.
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPublic(@RequestBody PublicChannelCreateRequest request) {
        ChannelResponse response = channelService.createPublic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 비공개 채널을 생성할 수 있다.
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> creatPrivate(@RequestBody PrivateChannelCreateRequest request) {
        ChannelResponse response = channelService.createPrivate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 공개 채널의 정보를 수정할 수 있다.
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelResponse> update(
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequest request
    ) {
        ChannelResponse response = channelService.update(channelId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 채널을 삭제할 수 있다.
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<ChannelResponse> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.notFound().build();
    }

    // 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
}
