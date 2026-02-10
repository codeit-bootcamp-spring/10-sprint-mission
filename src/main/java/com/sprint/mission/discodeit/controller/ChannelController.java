package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    @ResponseBody
    public Channel createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        return channelService.create(request);

    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    @ResponseBody
    public Channel createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        return channelService.create(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public Channel updatePublicChannel(@PathVariable UUID id,
                                       @RequestBody PublicChannelUpdateRequest request){

        return channelService.update(id, request);

    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteChannel(@PathVariable UUID id){
        channelService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ChannelDto> getChannels(@RequestParam UUID userId){
        return channelService.findAllByUserId(userId);
    }


}
