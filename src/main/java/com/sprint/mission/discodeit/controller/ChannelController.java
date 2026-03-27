package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
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
    log.info("Received POST /api/channels/public request - name: {}",
        request.name()); // 공개 채널 생성 요청 로그

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
    log.info("Received POST /api/channels/private request"); // 비공개 채널 생성 요청 로그

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
    log.info("Received GET /api/channels request - userId: {}", userId); // 특정 유저가 속한 채널 조회 요청 로그

    // 해당 유저가 속한 채널 리스트를 가져옴
    List<Channel> channels = channelService.findAllByUserId(userId);
    List<UUID> channelIds = channels.stream().map(Channel::getId).toList();

    // 루프 돌기 전 배치 쿼리로 데이터 미리 로드
    Map<UUID, Instant> lastMessageMap = channelService.getLastMessagesAtMap(channelIds);
    Map<UUID, List<User>> participantsMap = channelService.getParticipantsMap(channelIds);

    // 루프 돌며 데이터 조립
    List<ChannelDto> responseContents = channels.stream().map(channel -> {
      Instant lastMsg = lastMessageMap.get(channel.getId());

      List<UserDto> participantDtos = participantsMap.getOrDefault(channel.getId(), List.of())
          .stream().map(userMapper::toDto).toList();

      return channelMapper.toDto(channel, lastMsg, participantDtos);
    }).toList();

    return ResponseEntity.ok(responseContents);
  }

  @Override
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    log.info("Received PATCH /api/channels/{} request", channelId); // 채널 정보 수정 요청 로그

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
    log.info("Received DELETE /api/channels/{} request", channelId); // 채널 삭제 요청 로그

    channelService.deleteById(channelId);
    return ResponseEntity.noContent().build();
  }
}
