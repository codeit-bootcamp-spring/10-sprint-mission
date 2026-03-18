package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;
  private final ChannelMapper channelMapper;

  @Override
  @PostMapping("/public")
  public ResponseEntity<ChannelDto> create(
      @RequestBody PublicChannelCreateRequest request) {
    Channel channel = channelService.createPublicChannel(request.name(), request.description());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelMapper.toDto(channel));

  }

  @Override
  @PostMapping("/private")
  public ResponseEntity<ChannelDto> create(
      @RequestBody PrivateChannelCreateRequest request) {
    Channel channel = channelService.createPrivateChannel(request.participantIds());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelMapper.toDto(channel));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId) {
    List<Channel> channels = channelService.findAllByUserId(userId);
    List<ChannelDto> dtos = channels.stream()
        .map(channelMapper::toDto)
        .toList();

    return ResponseEntity.ok(dtos);
  }

  @Override
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    Channel channel = channelService.update(channelId, request.newName(), request.newDescription());

    return ResponseEntity.ok(channelMapper.toDto(channel));
  }

  @Override
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.deleteById(channelId);
    return ResponseEntity.noContent().build();
  }
}
