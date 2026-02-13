package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    /*
    **채널 관리**
    - [ ]  공개 채널을 생성할 수 있다.
    - [ ]  비공개 채널을 생성할 수 있다.
    - [ ]  공개 채널의 정보를 수정할 수 있다.
    - [ ]  채널을 삭제할 수 있다.
    - [ ]  특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
     */
    private final ChannelService channelService;

    @RequestMapping(path="/public",method = RequestMethod.POST)//나중에 소유자 지정
    public ResponseEntity<ChannelResponseDto> createPublicChannel(@Valid @RequestBody PublicChannelCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(dto));
    }

    @RequestMapping(path="/private",method = RequestMethod.POST)//나중에 소유자 지정
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(@Valid @RequestBody PrivateChannelCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(dto));
    }

    @RequestMapping(path="/{channelid}",method = RequestMethod.PATCH)//나중에 소유자가 정해진다면, 권한확인필요
    public ResponseEntity<ChannelResponseDto> updatePublicChannel(@PathVariable UUID channelid, @RequestBody ChannelUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(channelService.update(channelid,dto));
    }

    @RequestMapping(path="/{channelid}",method = RequestMethod.DELETE) //나중에 소유자가 정해진다면, 권한확인필요
    public ResponseEntity<?> deleteChannel(@PathVariable UUID channelid) {
        channelService.delete(channelid);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponseDto>> findChannelsByUser(@RequestHeader UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(channelService.findAllByUserId(userId));
    }

}
