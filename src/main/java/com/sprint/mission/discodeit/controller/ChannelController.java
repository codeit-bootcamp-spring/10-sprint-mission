package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ChannelResponseDto postPublicChannel(
            @RequestBody PublicChannelCreateRequestDto requestDto
    ){
        return channelService.createPublicChannel(requestDto);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ChannelResponseDto postPrivateChannel(
            @RequestBody PrivateChannelCreateRequestDto requestDto
    ){
        return channelService.createPrivateChannel(requestDto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ChannelResponseDto updateChannel(@RequestBody PublicChannelCreateRequestDto requestDto, @PathVariable UUID id){
        return channelService.update(new ChannelUpdateRequestDto(id,
                requestDto.name(), requestDto.description()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteChannel(@PathVariable UUID id){
        channelService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelResponseDto> getAllChannelWithUserId(
            @RequestParam UUID userId
    ){
        return channelService.findAllByUserId(userId);
    }

}
