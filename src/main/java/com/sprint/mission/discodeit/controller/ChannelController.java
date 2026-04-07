package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
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

    log.debug("HTTP 요청 - 채널 조회 userId={}", userId);

    List<Channel> channels = channelService.findAllByUser_Id(userId);

    List<ChannelDto> dtos = channels.stream()
            .map(channelMapper::toDto)
            .toList();

    return ResponseEntity.ok(dtos);
  }

  // POST /api/channels/public -> 201
  @RequestMapping(value = "/public", method = RequestMethod.POST)
  public ResponseEntity<ChannelDto> createPublic(@Valid @RequestBody PublicChannelCreateRequest request) {

    log.info("HTTP 요청 - 공개 채널 생성 name={}", request.name());
    Channel createdChannel = channelService.create(request);

    // channel -> Dto 변경
    ChannelDto dto = channelMapper.toDto(createdChannel);

    log.info("HTTP 응답 - 채널 생성 완료 channelId={}", createdChannel.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // POST /api/channels/private -> 201
  @RequestMapping(value = "/private", method = RequestMethod.POST)
  public ResponseEntity<ChannelDto> createPrivate(@Valid @RequestBody PrivateChannelCreateRequest request) {

    log.info("HTTP 요청 - 비공개 채널 생성 participants={}", request.participantIds());

    Channel createdChannel = channelService.create(request);

    ChannelDto dto = channelMapper.toDto(createdChannel);

    log.info("HTTP 응답 - 채널 생성 완료 channelId={}", createdChannel.getId());

    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  // PATCH /api/channels/{channelId}
  @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
  public ResponseEntity<ChannelDto> update(
          @PathVariable UUID channelId,
          @Valid @RequestBody PublicChannelUpdateRequest request
  ) {
    log.info("HTTP 요청 - 채널 수정 channelId={}", channelId);

    Channel updatedChannel = channelService.update(channelId, request);
    ChannelDto dto = channelMapper.toDto(updatedChannel);
    log.info("HTTP 응답 - 채널 수정 완료 channelId={}", channelId);
    return ResponseEntity.ok(dto);
  }

  // DELETE /api/channels/{channelId} -> 204(no content)(성공)
  @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    log.info("HTTP 요청 - 채널 삭제 channelId={}", channelId);
    channelService.delete(channelId);
    log.info("HTTP 요청 - 채널 삭제 channelId={}", channelId);
    return ResponseEntity.noContent().build();
  }
}
