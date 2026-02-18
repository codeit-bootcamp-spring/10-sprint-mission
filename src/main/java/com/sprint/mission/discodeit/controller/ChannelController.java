package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {

    private final ChannelService channelService;

    //공개 채널을 생성할 수 있다.
    @RequestMapping(value = "/{creatorId}/public", method = RequestMethod.POST)
    public ResponseEntity<?> createPublic(
            @PathVariable UUID creatorId,
            @ModelAttribute ChannelDto.CreatePublic request
    ) {
        ChannelDto.Response response = channelService.createPublic(creatorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //비공개 채널을 생성할 수 있다.
    @RequestMapping(value = "/{creatorId}/private", method = RequestMethod.POST)
    public ResponseEntity<?> createPrivate(
            @PathVariable UUID creatorId,
            @ModelAttribute ChannelDto.CreatePrivate request
    ) {
        ChannelDto.Response response = channelService.createPrivate(creatorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //공개 채널의 정보를 수정할 수 있다.
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> update(
            @PathVariable UUID channelId,
            @ModelAttribute ChannelDto.Update request
    ) {
        ChannelDto.Response response = channelService.update(channelId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //채널을 삭제할 수 있다.
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    //특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> findAllByUserId(@PathVariable UUID userId) {
        List<ChannelDto.Response> responses = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    //특정 채널 조회
    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<?> findById(@PathVariable UUID channelId) {
        ChannelDto.Response response = channelService.findById(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
