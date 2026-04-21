package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
@RequiredArgsConstructor
@Slf4j
public class ChannelController {
    private final ChannelService channelService;

    // 공용 채널 생성
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/public",method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> postPublicChannel(@Valid @RequestBody PublicChannelCreateRequest request){
        log.debug("공용 채널 생성 요청: 채널 이름 = {}, 채널 설명 = {}", request.getName(), request.getDescription());
        ChannelDto response = channelService.createPublic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // 사설 채널 생성
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/private",method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> postPrivateChannel(@Valid @RequestBody PrivateChannelCreateRequest request){
        log.debug("개인 채널 생성 요청: 참여자 수 = {}", request.getParticipantIds().size());
        ChannelDto response = channelService.createPrivate(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 유저가 속한 채널 전체 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> getAllChannelByUserId(@RequestParam(required = true) UUID userId){
        return ResponseEntity.ok(channelService.findAllChannelsByUserId(userId));
    }

    // 채널 업데이트
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto> updateChannel(@PathVariable("channelId") UUID id,
                                                     @Valid @RequestBody PublicChannelUpdateRequest dto){
        log.debug("채널 수정 요청: 수정할 채널 id = {}", id);
        ChannelDto response = channelService.update(id,dto);
        return ResponseEntity.ok(response);
    }

    // 채널 삭제
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable("channelId") UUID id){
        log.debug("채널 삭제 요청: 삭제할 채널 id = {}", id);
        channelService.delete(id);
        return ResponseEntity.noContent().build(); // noContent는 객체를 만든다는 뜻, build를 붙혀서 객체 만듬

    }
}
