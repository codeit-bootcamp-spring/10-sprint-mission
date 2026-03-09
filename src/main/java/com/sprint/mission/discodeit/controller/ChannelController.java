package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  //mapper 주입
  private final ChannelMapper channelMapper;

  // GET /api/channels?userId=...
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    List<Channel> channels = channelService.findAllByUserId(userId);

    List<ChannelDto> dtos = channels.stream()
            .map(channelMapper::toDto)
            .toList();

    return ResponseEntity.ok(dtos);
  }

  // POST /api/channels/public -> 201
  @RequestMapping(value = "/public", method = RequestMethod.POST)
  public ResponseEntity<ChannelDto> createPublic(@RequestBody PublicChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);

    // channel -> Dto 변경
    ChannelDto dto = channelMapper.toDto(createdChannel);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // POST /api/channels/private -> 201
  @RequestMapping(value = "/private", method = RequestMethod.POST)
  public ResponseEntity<ChannelDto> createPrivate(@RequestBody PrivateChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);

    ChannelDto dto = channelMapper.toDto(createdChannel);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // PATCH /api/channels/{channelId}
  @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
  public ResponseEntity<ChannelDto> update(
          @PathVariable UUID channelId,
          @RequestBody PublicChannelUpdateRequest request
  ) {
    Channel updatedChannel = channelService.update(channelId, request);
    ChannelDto dto = channelMapper.toDto(updatedChannel);
    return ResponseEntity.ok(dto);
  }

  // DELETE /api/channels/{channelId} -> 204(no content)(성공)
  @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }
}
