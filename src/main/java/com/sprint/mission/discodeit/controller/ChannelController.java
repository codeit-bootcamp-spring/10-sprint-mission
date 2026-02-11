package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelResponse createChannel(@RequestBody ChannelCreateRequest request) {
        return channelService.createChannel(request); //request dto에서 public인지 private인지 구분됨
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public ChannelResponse updateChannel(@RequestBody ChannelUpdateRequest request) {
        return channelService.updateChannel(request);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteChannel(@RequestParam UUID id) {
        channelService.deleteChannel(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<ChannelResponse> getChannels(@RequestParam UUID userId) {
        return channelService.findAllByUserId(userId);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<ChannelResponse> getAllChannels() {
        return channelService.getAllChannels();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ChannelResponse getChannel(@PathVariable UUID id) {
        return channelService.getChannel(id);
    }

    @RequestMapping(value = "/{channelId}/enter", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ChannelResponse enterChannel(@RequestParam UUID userId, @PathVariable UUID channelId) {
        return channelService.enterChannel(userId, channelId);
    }

    @RequestMapping(value = "/{channelId}/leave", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void leaveChannel(@RequestParam UUID userId, @PathVariable UUID channelId) {
        channelService.leaveChannel(userId, channelId);
    }
}