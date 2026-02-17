package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channeldto.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channeldto.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    @ResponseBody
    public ChannelResponseDTO createPublicChannel(@RequestBody PublicChannelCreateDTO req){
        return channelService.createPublicChannel(req);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    @ResponseBody
    public ChannelResponseDTO createPrivateChannel(@RequestBody PrivateChannelCreateDTO req){
        return channelService.createPrivateChannel(req);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public List<ChannelResponseDTO> getChannels(@PathVariable UUID userId){
        return channelService.findAllByUserId(userId);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteChannel(@PathVariable UUID channelId){
        channelService.delete(channelId);
    }

    @RequestMapping( method = RequestMethod.PATCH)
    @ResponseBody
    public ChannelResponseDTO updateChannel(@RequestBody ChannelUpdateRequestDTO req){
        return channelService.update(req);
    }






}
