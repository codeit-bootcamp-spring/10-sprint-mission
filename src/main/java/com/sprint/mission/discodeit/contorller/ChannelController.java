package com.sprint.mission.discodeit.contorller;


import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<UUID> createPublicChannel(
            @RequestBody CreatePublicChannelRequest request
    ) {
        UUID channelId = channelService.createPublicChannel(request);

        return ResponseEntity.ok(channelId);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<UUID> createPrivateChannel(
            @RequestBody CreatePrivateChannelRequest request
    ) {
        UUID channelId = channelService.createPrivateChannel(request);

        return ResponseEntity.ok(channelId);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<ChannelResponse> findChannelByChannelId(
            @PathVariable UUID channelId
    ) {
        ChannelResponse response =
                channelService.findChannelByChannelId(channelId);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponse>> findAllChannelsByUserId(
            @RequestParam UUID userId
    ) {
        List<ChannelResponse> responses =
                channelService.findAllChannelsByUserId(userId);

        return ResponseEntity.ok(responses);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ChannelResponse> updateChannelInfo(
            @RequestBody UpdateChannelRequest request
    ) {
        ChannelResponse response =
                channelService.updateChannelInfo(request);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(
            @PathVariable UUID channelId
    ) {
        channelService.deleteChannel(channelId);

        return ResponseEntity.noContent().build();
    }
}
