package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import org.apache.catalina.valves.rewrite.RewriteCond;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api")
public class ChannelController {
    private final ChannelService channelService;
    private final UserService userService;

    public ChannelController(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    @RequestMapping(value = "/channel/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> postPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        ChannelDto channelDto = channelService.find(channel.getId());
        return new ResponseEntity<>(channelDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/channel/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> postPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        ChannelDto channelDto = channelService.find(channel.getId());
        return new ResponseEntity<>(channelDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/channel/{channelId}", method = RequestMethod.PUT)
    public ResponseEntity<ChannelDto> putPublicChannel(@PathVariable UUID channelId,
                                                       @RequestBody PublicChannelUpdateRequest request) {
        channelService.update(channelId, request);
        ChannelDto channelDto = channelService.find(channelId);
        return new ResponseEntity<>(channelDto, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/user/{userId}/channels", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> getChannelAllByUserId(@PathVariable UUID userId) {
        List<ChannelDto> channelDtoList = channelService.findAllByUserId(userId);
        return new ResponseEntity<>(channelDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/channel/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
        System.out.println("채널 삭제 시작");
        channelService.delete(channelId);
        System.out.println("채널 삭제 완료");
        return ResponseEntity.ok().build();
    }
}
