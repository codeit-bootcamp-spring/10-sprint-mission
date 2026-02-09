package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ChannelDto.Response> createChannel(
            @RequestBody @Valid ChannelDto.CreateRequest request) {
        ChannelDto.Response response = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{channel-id}")
    public ResponseEntity<ChannelDto.Response> updateChannel(
            @PathVariable("channel-id") UUID channelId,
            @RequestBody @Valid ChannelDto.UpdatePublicRequest request) {
        ChannelDto.Response response = channelService.update(channelId, request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{channel-id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{channel-id}")
    public ResponseEntity<ChannelDto.Response> findChannel(@PathVariable("channel-id") UUID channelId) {
        ChannelDto.Response response = channelService.find(channelId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto.Response>> findAllByUser(@RequestParam("user-id") UUID userId) {
        List<ChannelDto.Response> response = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(response);
    }

}
