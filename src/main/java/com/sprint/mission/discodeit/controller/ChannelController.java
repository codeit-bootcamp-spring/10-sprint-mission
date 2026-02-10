package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(
            ChannelService channelService){
        this.channelService = channelService;
    }

    // 공개 채널 생성
    @RequestMapping(value = "/public",method = RequestMethod.POST)
    public ChannelResponse postPublicChannel(@RequestBody PublicChannelCreateRequest dto){
        UUID channelId = channelService.createPublic(dto);
        return channelService.find(channelId);
    }

    // 비공개 채널 생성
    @RequestMapping(value = "/private",method = RequestMethod.POST)
    public ChannelResponse postPrivateChannel(@RequestBody PrivateChannelCreateRequest dto){
        UUID channelId = channelService.createPrivate(dto);
        return channelService.find(channelId);
    }

    // 채널 단건 조회
    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ChannelResponse getChannel(@PathVariable UUID channelId) {
        return channelService.find(channelId);
    }

    // 공개채널의 정보 수정
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PUT)
    public ChannelResponse putChannelId(
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequest dto
    ) {
        if(dto == null || dto.channelId() == null) {
            throw new IllegalArgumentException("channelId null이 될 수 없습니다.");
        }
        if(!channelId.equals(dto.channelId())) {
            throw new IllegalArgumentException("path channelId와 body channelId가 일치해야 합니다.");
        }
        return channelService.update(dto);
    }

    // 채널 삭제
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public void deleteChannelId(@PathVariable UUID channelId){
        channelService.delete(channelId);
    }

    //특정 사용자가 볼 수 있는 채널 목록 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelResponse> getChannelByUserId(@RequestParam UUID userId){
        return channelService.findAllByUserId(userId);
    }
}
