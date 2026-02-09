package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
