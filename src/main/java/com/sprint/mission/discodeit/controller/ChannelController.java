package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
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
  private final UserMapper userMapper;

  @Override
  @PostMapping("/public")
  public ResponseEntity<ChannelDto> createPublicChannel(
      @RequestBody PublicChannelCreateRequest request) {
    Channel channel = channelService.createPublicChannel(request.name(), request.description());

    ChannelDto response = channelMapper.toDto(
        channel,
        channelService.getLastMessageAt(channel.getId()),
        List.of() // 생성 직후엔 참여자 DTO 리스트가 비어있음
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Override
  @PostMapping("/private")
  public ResponseEntity<ChannelDto> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest request) {
    Channel channel = channelService.createPrivateChannel(request.participantIds());

    Instant lastMessageAt = channelService.getLastMessageAt(channel.getId());
    List<UserDto> participantDtos = channelService.getParticipants(channel.getId()).stream()
        .map(userMapper::toDto)
        .toList();

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelMapper.toDto(channel, lastMessageAt, participantDtos));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId) {
    List<Channel> channels = channelService.findAllByUserId(userId);

    List<ChannelDto> channelDtos = channels.stream()
        .map(channel -> {
          Instant lastMessage = channelService.getLastMessageAt(channel.getId());
          List<UserDto> participantDtos = channelService.getParticipants(channel.getId()).stream()
              .map(userMapper::toDto)
              .toList();
          return channelMapper.toDto(channel, lastMessage, participantDtos);
        })
        .toList();

    return ResponseEntity.ok(channelDtos);
  }

  @Override
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    Channel channel = channelService.update(channelId, request.newName(), request.newDescription());

    Instant lastMessage = channelService.getLastMessageAt(channel.getId());
    List<UserDto> participantDtos = channelService.getParticipants(channel.getId()).stream()
        .map(userMapper::toDto)
        .toList();

    return ResponseEntity.ok(channelMapper.toDto(channel, lastMessage, participantDtos));
  }

  @Override
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.deleteById(channelId);
    return ResponseEntity.noContent().build();
  }
}
