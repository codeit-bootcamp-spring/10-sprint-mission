package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelFindResponse;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    // public Channel 생성
    @RequestMapping(value = "/public", method=RequestMethod.POST)
    public ChannelResponse postPublicChannel(@RequestBody ChannelCreateRequestPublic request){
        return channelService.createPublic(request);
    }

    // private Channel 생성
    @RequestMapping(value="/private", method=RequestMethod.POST)
    public ChannelResponse postPrivateChannel(@RequestBody ChannelCreateRequestPrivate request){
        return channelService.createPrivate(request);
    }

    // Channel 단건 조회
    @RequestMapping(value="/{channel-id}", method=RequestMethod.GET)
    public ChannelFindResponse getChannel(@PathVariable("channel-id") UUID channelID){
        return channelService.find(channelID);
    }

    // User 별 Channel 다건 조회 by user
    @RequestMapping(method=RequestMethod.GET)
    public List<ChannelFindResponse> getAllChannels(@RequestParam("userID") UUID userID){
        return channelService.findAllByUserID(userID);
    }

    // public Channel 수정
    @RequestMapping(method=RequestMethod.PATCH)
    public ChannelResponse updateChannel(@RequestBody ChannelUpdateRequest request){
        return channelService.updateName(request);
    }

    // Channel 삭제
    @RequestMapping(value="/{channel-id}", method=RequestMethod.DELETE)
    public void deleteChannel(@PathVariable("channel-id") UUID channelID){
        channelService.deleteChannel(channelID);
    }
}
