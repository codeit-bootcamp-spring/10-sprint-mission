package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;

    //  [ ] 공개 채널을 생성할 수 있다.
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> postPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        Channel created = channelService.create(request);
        ChannelDto body = channelService.find(created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    //  [ ] 비공개 채널을 생성할 수 있다.
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> postPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        Channel created = channelService.create(request);
        ChannelDto body = channelService.find(created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    //  [ ] 공개 채널의 정보를 수정할 수 있다.
    @RequestMapping(value = "/{channel-id}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto> updateChannel(
            @PathVariable("channel-id") UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {
        channelService.update(channelId, request);
        return ResponseEntity.ok(channelService.find(channelId));
    }

    //  [ ] 채널을 삭제할 수 있다.
    @RequestMapping(value = "/{channel-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    //  [ ] 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
    @RequestMapping(value = "/users/{user-id}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> getChannelsByUser(@PathVariable("user-id") UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }


}
