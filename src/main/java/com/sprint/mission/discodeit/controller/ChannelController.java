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

    // Public 채널 생성
    @RequestMapping(value = "createPublic", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto.response> createPublicChannel(@RequestBody ChannelDto.createPublicRequest createReq) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(channelService.createChannel(createReq));
    }

    // Private 채널 생성
    @RequestMapping(value = "createPrivate", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto.response> createPrivateChannel(@RequestBody ChannelDto.createPrivateRequest createReq) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(channelService.createChannel(createReq));
    }

    // 채널 단일 조회(UUID)
    @RequestMapping(value = "{channel-id}", method = RequestMethod.GET)
    public ResponseEntity<ChannelDto.response> findUser(@PathVariable("channel-id") UUID channelId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(channelService.findChannel(channelId));
    }

    // 채널 단일 조회(Title)
    @RequestMapping(params = "title", method = RequestMethod.GET)
    public ResponseEntity<ChannelDto.response> findUser(@RequestParam String title) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(channelService.findChannelByTitle(title));
    }

    // 특정 사용자의 Public + Private 채널 조회
    @RequestMapping(value = "find/{user-id}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto.response>> findAllByUserId(@PathVariable("user-id") UUID userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(channelService.findAllByUserId(userId));
    }

    // Public 채널 수정
    @RequestMapping(value = "{channel-id}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto.response> updatePublicChannel(@PathVariable("channel-id") UUID channelId,
                                                                   @RequestBody ChannelDto.updatePublicRequest updateReq) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(channelService.updateChannel(channelId, updateReq));
    }

    // 채널 삭제
    @RequestMapping(value = "{channel-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 채널 참가
    @RequestMapping(value = "/{channel-id}/join/{user-id}", method = RequestMethod.PATCH)
    public ResponseEntity<Void> joinChannel(@PathVariable("channel-id") UUID channelId,
                                            @PathVariable("user-id") UUID userId) {
        channelService.joinChannel(channelId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 채널 퇴장
    @RequestMapping(value = "/{channel-id}/leave/{user-id}", method = RequestMethod.PATCH)
    public ResponseEntity<Void> leaveChannel(@PathVariable("channel-id") UUID channelId,
                                            @PathVariable("user-id") UUID userId) {
        channelService.leaveChannel(channelId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
