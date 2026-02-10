package com.sprint.mission.discodeit.channel.controller;

import com.sprint.mission.discodeit.channel.dto.ChannelCreatePrivateRequest;
import com.sprint.mission.discodeit.channel.dto.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.channel.dto.ChannelResponse;
import com.sprint.mission.discodeit.channel.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.mapper.ChannelMapper;
import com.sprint.mission.discodeit.channel.service.ChannelService;
import com.sprint.mission.discodeit.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(method = RequestMethod.POST,
            params = "type=PUBLIC")
    public ChannelResponse createPublicChannel (@RequestBody ChannelCreatePublicRequest request){
        return channelService.createPublic(request);
    }

    @RequestMapping(method = RequestMethod.POST,
            params = "type=PRIVATE")
    public ChannelResponse createPrivateChannel(@RequestBody ChannelCreatePrivateRequest request){
        return channelService.createPrivate(request);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ChannelResponse updatePublicChannel(@RequestBody ChannelUpdateRequest request) {
        return channelService.update(request);
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public void deleteChannel(@PathVariable UUID id) {
         channelService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET, params = "memberId")
    public List<ChannelResponse> findAllByUserId(@RequestParam UUID memberId) {
        return channelService.findAllByUserId(memberId);
    }

}
