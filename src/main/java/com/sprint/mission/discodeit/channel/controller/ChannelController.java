package com.sprint.mission.discodeit.channel.controller;

import com.sprint.mission.discodeit.channel.dto.*;
import com.sprint.mission.discodeit.channel.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<PublicChannelInfo> createChannel(
            @RequestBody PublicChannelCreateInfo channelInfo
    ) {
        return ResponseEntity.ok(channelService.createPublicChannel(channelInfo));
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<PrivateChannelInfo> createChannel(
            @RequestBody PrivateChannelCreateInfo channelInfo
    ) {
        return ResponseEntity.ok(channelService.createPrivateChannel(channelInfo));
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<ChannelInfo> getChannel(@PathVariable UUID channelId) {
        return ResponseEntity.ok(channelService.findChannel(channelId));
    }

    @RequestMapping(method = RequestMethod.GET, consumes = "application/json")
    public ResponseEntity<List<ChannelInfo>> getAllVisibleChannels(@RequestBody FindChannelInfo findChannelInfo) {
        return ResponseEntity.ok(channelService.findAllByUserId(findChannelInfo.userId()));
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH, consumes = "application/json")
    public ResponseEntity<Void> updateChannel(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelCreateInfo channelInfo
    ) {
        channelService.updateChannel(channelId, channelInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }
}
