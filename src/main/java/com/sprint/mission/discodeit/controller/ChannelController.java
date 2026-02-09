package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ChannelDto.Response createPublicChannel(@RequestBody ChannelDto.CreatePublicRequest createPublicRequest) {
        return channelService.createPublicChannel(createPublicRequest);
    }

    // TODO: UUID 형식이 맞으면 비공개 채널 생성이 가능한 상태, 추후 전역 처리 시 검증 로직 추가 예정
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ChannelDto.Response createPrivateChannel(@RequestBody ChannelDto.CreatePrivateRequest createPrivateRequest) {
        return channelService.createPrivateChannel(createPrivateRequest);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ChannelDto.Response update(@PathVariable UUID id,
                                      @RequestBody ChannelDto.UpdateRequest update) {
        return channelService.update(update);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID id) {
        channelService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto.Response> getChannelsByUserId(@RequestParam(name = "id") UUID id) {
        return channelService.findAllByUserId(id);
    }
}
