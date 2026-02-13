package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;


    //public 채널 생성
    // Channel Name
    @RequestMapping(value="/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPublic(@RequestBody PublicChannelCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPublicChannel(request));
    }

    //private 채널 생성
    @RequestMapping(value="/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPrivate(@RequestBody PrivateChannelCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPrivateChannel(request));
    }
    //채널 정보 수정
    @RequestMapping(value="/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelResponse> updateChannel(@PathVariable UUID channelId,
                                                         @RequestBody ChannelUpdateRequest body){
        ChannelUpdateRequest request = new ChannelUpdateRequest(
                channelId,
                body.newName()
        );
        ChannelResponse response = channelService.updateChannel(request);
        return ResponseEntity.ok(response);
    }
    // 채널 삭제
    @RequestMapping(value="/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId){
        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }
    //user가 속한 채널 리스트 조회
    @RequestMapping(value= "/{userId}/channelList", method=RequestMethod.GET)
    public ResponseEntity<List<ChannelResponse>> getChannels(@PathVariable UUID userId){
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
    // 채널 참가..? 추가하기.



}
