package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
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

    // TODO: 채널 생성자 정보가 없으므로 로직 추가 예정. (채널을 누가 만들었는지 모름)
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
        if (!id.equals(update.id())) throw new BusinessException(ErrorCode.PATH_ID_MISMATCH);

        return channelService.update(update);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID id) {
        channelService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto.Response> getChannelsByUserId(@RequestParam(name = "userId") UUID userId) {
        return channelService.findAllByUserId(userId);
    }
    // TODO: 채널 입장과 퇴장 로직 구현 예졍
}
