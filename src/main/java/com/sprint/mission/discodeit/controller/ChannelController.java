package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  // 공개 채널 생성
  @RequestMapping(value = "/public", method = RequestMethod.POST)
  public ChannelResponseDto createPublicChannel(@RequestBody PublicChannelCreateDto dto) {
    return channelService.createPublic(dto);
  }

  // 비공개 채널 생성
  @RequestMapping(value = "/private", method = RequestMethod.POST)
  public ChannelResponseDto createPrivateChannel(@RequestBody PrivateChannelCreateDto dto) {
    return channelService.createPrivate(dto);
  }

  // 공개 채널 정보 수정
  @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
  public ChannelResponseDto updatePublicChannel(@PathVariable UUID channelId,
      @RequestBody ChannelUpdateDto dto) {
    return channelService.update(channelId, dto);
  }

  // 채널에 멤버 추가
  @RequestMapping(value = "/{channel-id}/join", method = RequestMethod.POST)
  public ChannelResponseDto joinChannel(@PathVariable("channel-id") UUID channelId,
      @RequestParam UUID userId) {
    return channelService.joinChannel(userId, channelId);

  }

  // 채널 삭제
  @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
  public void delete(@PathVariable UUID channelId) {

    channelService.delete(channelId);
  }

  // 모든 채널 목록 조회
  @RequestMapping(value = "/{user-id}", method = RequestMethod.GET)
  public List<ChannelResponseDto> findAll(@PathVariable("user-id") UUID userId) {
    return channelService.findAllByUserId(userId);
  }
}
